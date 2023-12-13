package com.cheernota.riaratingreport.service.impl;

import com.cheernota.riaratingreport.dto.ResearchV2JsonDto;
import com.cheernota.riaratingreport.service.ResearchHandler;
import com.cheernota.riaratingreport.service.ResearchService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResearchHandlerV2Impl implements ResearchHandler<ResearchV2JsonDto> {

    private final ResearchService researchService;

    @Override
    public void handleResearch(ResearchV2JsonDto researchDto, String researchCode) {
        var dataList = researchDto.getData();
        if (CollectionUtils.isEmpty(dataList)) {
            log.warn("Research with code {} hasn`t data", researchCode);
            return;
        }

        var regionList = getFilteredList(dataList, "Регион", "Город", "Область", "Отрасль");
        var placeList = getFilteredList(dataList, "Место", "Позиция");
        if (regionList.isEmpty() || placeList.isEmpty()) {
            log.warn("Research with code {} hasn`t region or place column", researchCode);
            return;
        }

        var regionNameSet = researchService.getRegionNameSet();
        for (var i = 0; i < regionList.size() && i < placeList.size(); i++) {
            var regionName = regionList.get(i);
            var place = placeList.get(i);
            if (StringUtils.isNotEmpty(regionName) && NumberUtils.isDigits(place)) {
                regionName = handleRegionName(regionName);
                if (!regionNameSet.contains(regionName)) {
                    researchService.createRegion(regionName);
                }
                researchService.checkAndCreateRegionResearch(place, regionName, researchCode);
            }
        }
    }

    private static List<String> getFilteredList(List<ResearchV2JsonDto.InfoData> dataList, String... filter) {
        return dataList.stream()
                .filter(data -> StringUtils.isNotEmpty(data.getHeader())
                        && StringUtils.containsAnyIgnoreCase(data.getHeader().trim(), filter))
                .map(ResearchV2JsonDto.InfoData::getData)
                .findFirst()
                .orElseGet(Collections::emptyList);
    }

}
