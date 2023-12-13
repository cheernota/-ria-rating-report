package com.cheernota.riaratingreport.service.impl;

import com.cheernota.riaratingreport.dto.ResearchV1JsonDto;
import com.cheernota.riaratingreport.dto.ResearchV1JsonDto.SlideData;
import com.cheernota.riaratingreport.service.ResearchHandler;
import com.cheernota.riaratingreport.service.ResearchService;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResearchHandlerV1Impl implements ResearchHandler<ResearchV1JsonDto> {

    private final ResearchService researchService;

    @Override
    public void handleResearch(ResearchV1JsonDto researchDto, String researchCode) {
        var slideList = Optional.ofNullable(researchDto.getSlides())
                .map(x -> x.get(0))
                .map(ResearchV1JsonDto.Slide::getData);
        if (slideList.isEmpty()) {
            log.warn("Research with code {} hasn`t data for rating", researchCode);
            return;
        }

        var regionColumnId = getRegionColumnId(slideList);
        var placeColumnId = getFilteredColumnId(slideList, getFilteredHeaderId(slideList, "Место", "Позиция"));

        // ignore doc if next values were not found
        if (regionColumnId == -1 || placeColumnId == -1) {
            log.warn("Research with code {} hasn`t some data for rating", researchCode);
            return;
        }

        var regionNameSet = researchService.getRegionNameSet();
        var dataList = slideList.get().getAppData();
        for (final var data : dataList) {
            String regionName = data.get(regionColumnId).getDataText();
            String regionPlace = data.get(placeColumnId).getDataText();
            if (StringUtils.isNotEmpty(regionName) && NumberUtils.isDigits(regionPlace)) {
                regionName = handleRegionName(regionName);
                if (!regionNameSet.contains(regionName)) {
                    researchService.createRegion(regionName);
                }
                researchService.checkAndCreateRegionResearch(regionPlace, regionName, researchCode);
            }
        }
    }

    private static Integer getRegionColumnId(Optional<SlideData> slideList) {
        return slideList.stream()
                .map(data -> data.getApp().getAppMap())
                .filter(appMap -> Objects.nonNull(appMap) && "russia-regions".equals(appMap.getAppMapType()))
                .map(ResearchV1JsonDto.AppMap::getRegionColumnId)
                .findFirst()
                .orElse(-1);
    }

    private static Integer getFilteredHeaderId(Optional<SlideData> slideList, String... filter) {
        return slideList.stream()
                .flatMap(data -> data.getHeaders().stream())
                .filter(header -> StringUtils.containsAnyIgnoreCase(header.getHeaderText(), filter))
                .map(ResearchV1JsonDto.SlideDataHeader::getHeaderId)
                .findFirst()
                .orElse(-1);
    }

    private static Integer getFilteredColumnId(Optional<SlideData> slideList, Integer columnHeaderId) {
        return slideList.stream()
                .flatMap(data -> data.getApp().getAppColumns().stream())
                .filter(appColumn -> appColumn != null && columnHeaderId.equals(appColumn.getAppHeaderId()))
                .map(ResearchV1JsonDto.AppColumn::getAppColumnId)
                .findFirst()
                .orElse(-1);
    }
}
