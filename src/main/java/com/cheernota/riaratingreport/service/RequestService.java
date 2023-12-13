package com.cheernota.riaratingreport.service;

import com.cheernota.riaratingreport.dto.ResearchJsonDto;
import com.cheernota.riaratingreport.dto.ResearchV1JsonDto;
import com.cheernota.riaratingreport.dto.ResearchV2JsonDto;
import com.cheernota.riaratingreport.dto.request.PaginationRqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestService {

    private static final String HOST_URL = "https://riarating.ru/";
    private static final String USER_AGENT = "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/119.0.0.0 Safari/537.36";
    private final RestTemplate restTemplate;

    public static Pageable getPageable(PaginationRqDto pagination) {
        return PageRequest.of(pagination.getPageNumber(), pagination.getPageSize());
    }

    public static Document getDocumentByUrl(String connectUrl) {
        Document document = null;
        try {
            document = Jsoup.connect(HOST_URL.concat(connectUrl))
                    .userAgent(USER_AGENT)
                    .timeout(10000) // 10 sec
                    .get();
        } catch (Exception ex) {
            log.error(String.format("Couldn't make connection via url: %s", connectUrl), ex);
        }
        return document;
    }

    public ResearchJsonDto requestResearchDto(String jsonUrl) {
        ResearchJsonDto researchJsonDto = null;
        String fullUrl = jsonUrl.concat("/data/data.json");
        try {
            researchJsonDto = restTemplate.getForObject(fullUrl, ResearchV1JsonDto.class);
        } catch (RestClientException e) {
            if (e.getCause() instanceof HttpMessageNotReadableException) {
                log.error(String.format("Can`t parse json via url %s:", fullUrl), ExceptionUtils.getRootCause(e));
                return null;
            }
            try {
                fullUrl = jsonUrl.concat("/data.json");
                researchJsonDto = restTemplate.getForObject(fullUrl, ResearchV2JsonDto.class);
            } catch (RestClientException ex) {
                log.error(String.format("Can`t parse json via url %s:", fullUrl), ExceptionUtils.getRootCause(e));
            }
        }
        return researchJsonDto;
    }
}