package com.cheernota.riaratingreport.service;

import com.cheernota.riaratingreport.dto.ResearchJsonDto;
import com.cheernota.riaratingreport.dto.ResearchV1JsonDto;
import com.cheernota.riaratingreport.dto.ResearchV2JsonDto;
import com.cheernota.riaratingreport.exception.NotFoundException;
import com.cheernota.riaratingreport.repository.ResearchRepository;
import com.cheernota.riaratingreport.service.impl.ResearchHandlerV1Impl;
import com.cheernota.riaratingreport.service.impl.ResearchHandlerV2Impl;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexService {

    private static final String START_DATE = "2018-01-01";
    private static final DateTimeFormatter REQUEST_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter RESPONSE_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final ResearchHandlerV1Impl researchHandlerV1;
    private final ResearchHandlerV2Impl researchHandlerV2;
    private final ResearchRepository researchRepository;
    private final RequestService requestService;
    private final ResearchService researchService;

    /**
     * This method starts by cron and makes next steps:</p>
     * 1) get from DB last update date. If it`s null, get earlier date to get all pages;
     * 2) make http request, parse articles by jsoup;
     * 3) fill tables if needed.
     */
    public void indexResearches() {
        LocalDate currentDate = getStartDate();
        var connectUrl = String.format("regions/?date_start=%s&date=%s", currentDate.format(REQUEST_DATE_FORMAT),
                LocalDate.now(ZoneId.of("Europe/Moscow")).format(REQUEST_DATE_FORMAT));
        Document regionsDoc = RequestService.getDocumentByUrl(connectUrl);

        if (regionsDoc == null || !regionsDoc.hasText()) {
            throw new NotFoundException("Content was not found by url: ".concat(connectUrl));
        }

        List<String> infoUrls = new ArrayList<>();
        extractAndAddInfoUrls(regionsDoc, infoUrls);
        extractAndAddInfoUrlsForMoreDocs(regionsDoc, infoUrls);
        log.info("The size of infographic urls: {}", infoUrls.size());

        final var researchCodeSet = new HashSet<>(researchRepository.findDistinctResearchCodes());
        handleUrls(infoUrls, researchCodeSet);
        log.info("All urls handled...");
    }

    private void handleUrls(List<String> infoUrls, HashSet<String> researchCodeSet) {
        for (final var infoUrl : infoUrls) {
            Document infoDocument = RequestService.getDocumentByUrl(infoUrl);
            if (infoDocument != null && infoDocument.hasText()) {
                LocalDate researchDate = Optional.ofNullable(infoDocument.getElementsByClass("article-header__date").first())
                        .map(Element::text)
                        .map(x -> LocalDate.parse(x, RESPONSE_DATE_FORMAT))
                        .orElse(null);

                var researchName = Optional.ofNullable(infoDocument.getElementsByClass("article-header__text").first())
                        .map(Element::text)
                        .orElse(StringUtils.EMPTY);

                var jsonUrl = Optional.ofNullable(infoDocument.getElementsByClass("article-infographics").first())
                        .map(elem -> elem.select("div"))
                        .map(elem -> elem.select("script"))
                        .map(elem -> elem.attr("src"))
                        .orElse(StringUtils.EMPTY);

                if (!jsonUrl.isEmpty()) {
                    log.debug("jsonUrl before handling: {}", jsonUrl);
                    jsonUrl = jsonUrl.substring(0, jsonUrl.lastIndexOf("/"));
                    while (jsonUrl.endsWith("/")) {
                        jsonUrl = jsonUrl.substring(0, jsonUrl.length() - 1);
                    }
                    var researchCode = jsonUrl.substring(jsonUrl.lastIndexOf("/") + 1);
                    if (researchCode.contains("%")) {
                        jsonUrl = jsonUrl.substring(0, jsonUrl.lastIndexOf("/"));
                        researchCode = jsonUrl.substring(jsonUrl.lastIndexOf("/") + 1);
                    }

                    if (StringUtils.isEmpty(researchCode) || StringUtils.isEmpty(jsonUrl)) {
                        log.info("researchCode {} or jsonUrl {} is empty", researchCode, jsonUrl);
                        continue;
                    }
                    if (researchCodeSet.contains(researchCode)) {
                        log.info("A research with such a code {} already exists", researchCode);
                        continue;
                    }

                    ResearchJsonDto researchJsonDto = requestService.requestResearchDto(jsonUrl);
                    if (researchJsonDto instanceof ResearchV1JsonDto) {
                        researchService.saveNewResearchEntity(researchDate, researchName, researchCode, researchJsonDto, 1);
                        researchHandlerV1.handleResearch((ResearchV1JsonDto) researchJsonDto, researchCode);
                    } else if (researchJsonDto instanceof ResearchV2JsonDto) {
                        researchService.saveNewResearchEntity(researchDate, researchName, researchCode, researchJsonDto, 2);
                        researchHandlerV2.handleResearch((ResearchV2JsonDto) researchJsonDto, researchCode);
                    } else {
                        log.error("Unknown json via url {} for research name {}", jsonUrl, researchName);
                    }
                }
            }
        }
    }

    private static void extractAndAddInfoUrlsForMoreDocs(Document rootDocument, List<String> infoUrls) {
        var ajaxUrl = Optional.ofNullable(rootDocument.getElementsByClass("rubric-list__get-more").first())
                .map(elem -> elem.attr("data-url"))
                .orElse(StringUtils.EMPTY);
        if (StringUtils.isNotEmpty(ajaxUrl)) {
            Document nextDocument = RequestService.getDocumentByUrl(ajaxUrl.replace("&amp;", "&"));
            if (nextDocument != null && nextDocument.hasText()) {
                extractAndAddInfoUrls(nextDocument, infoUrls);
                extractAndAddInfoUrlsForMoreDocs(nextDocument, infoUrls);
            }
        }
    }

    private static void extractAndAddInfoUrls(Document document, List<String> infografikaUrls) {
        Elements articleAnchors = document.getElementsByClass("rubric-list__article-anchor");
        for (final var anchor : articleAnchors) {
            String anchorUrl = anchor.attr("href");
            if (StringUtils.startsWith(anchorUrl, "/infografika")) {
                infografikaUrls.add(anchorUrl);
            }
        }
    }

    private LocalDate getStartDate() {
        var lastResearch = researchRepository.findFirstByOrderByResearchDateDesc();
        return lastResearch.map(research -> research.getResearchDate().plusDays(1))
                .orElseGet(() -> LocalDate.parse(START_DATE, DateTimeFormatter.ISO_DATE));
    }
}