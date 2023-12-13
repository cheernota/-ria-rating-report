package com.cheernota.riaratingreport.service;

import static com.cheernota.riaratingreport.util.ResearchSpecification.buildResearchSpecification;

import com.cheernota.riaratingreport.dto.ResearchJsonDto;
import com.cheernota.riaratingreport.dto.request.ResearchFilterRqDto;
import com.cheernota.riaratingreport.dto.response.RegionPlaceRsDto;
import com.cheernota.riaratingreport.dto.response.RegionRsDto;
import com.cheernota.riaratingreport.dto.response.ResearchResultRsDto;
import com.cheernota.riaratingreport.dto.response.ResearchRsDto;
import com.cheernota.riaratingreport.entity.Region;
import com.cheernota.riaratingreport.entity.RegionResearch;
import com.cheernota.riaratingreport.entity.Research;
import com.cheernota.riaratingreport.exception.NotFoundException;
import com.cheernota.riaratingreport.repository.RegionRepository;
import com.cheernota.riaratingreport.repository.RegionResearchRepository;
import com.cheernota.riaratingreport.repository.ResearchRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResearchService {

    private final RegionRepository regionRepository;
    private final ResearchRepository researchRepository;
    private final RegionResearchRepository regionResearchRepository;
    private final ObjectMapper objectMapper;

    public List<RegionPlaceRsDto> getResearchResultListById(Integer researchId) {
        List<RegionResearch> regionResearchList = regionResearchRepository.findAllByResearchId(researchId);
        if (CollectionUtils.isEmpty(regionResearchList)) {
            throw new NotFoundException("Content was not found for researchId: " + researchId);
        }

        return regionResearchList.stream()
                .map(regionResearch -> RegionPlaceRsDto.builder()
                        .regionName(regionResearch.getRegion().getRegionName())
                        .place(regionResearch.getRegionPlace())
                        .build())
                .collect(Collectors.toList());
    }

    public ResearchResultRsDto getResearchListByFilter(ResearchFilterRqDto filter, Pageable pageable) {
        Specification<Research> spec = Specification.where(buildResearchSpecification(filter));
        List<Research> researchList = researchRepository.findAll(spec, pageable).getContent();
        var totalCount = researchRepository.count(spec);
        List<ResearchRsDto> regionDtoList = new ArrayList<>();

        for (final var research : researchList) {
            regionDtoList.add(ResearchRsDto.builder()
                    .researchId(research.getResearchId())
                    .researchName(research.getResearchName())
                    .researchDate(research.getResearchDate())
                    .build());
        }

        return ResearchResultRsDto.builder()
                .list(regionDtoList)
                .totalCount(totalCount)
                .build();
    }

    public List<RegionRsDto> getRegionList() {
        List<Region> regionList = regionRepository.findAll();
        List<RegionRsDto> regionDtoList = new ArrayList<>();

        for (final var region : regionList) {
            Double average = BigDecimal.valueOf(
                            region.getRegionResearchSet().stream()
                                    .map(RegionResearch::getRegionPlace)
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.averagingInt(Integer::intValue)))
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();

            regionDtoList.add(RegionRsDto.builder()
                    .regionName(region.getRegionName())
                    .averagePlace(average)
                    .build());
        }
        regionDtoList.sort(Comparator.comparingDouble(RegionRsDto::getAveragePlace));
        return regionDtoList;
    }

    public Set<String> getRegionNameSet() {
        return new HashSet<>(regionRepository.findDistinctRegionNames());
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveNewResearchEntity(LocalDate researchDate, String researchName, String researchCode,
                                      ResearchJsonDto researchJsonDto, Integer version) {
        String json = StringUtils.EMPTY;
        try {
            json = objectMapper.writeValueAsString(researchJsonDto);
        } catch (JsonProcessingException e) {
            log.error(String.format("Unknown error while converting object to string %s", researchJsonDto.toString()), e);
        }
        var researchEntity = new Research();
        researchEntity.setResearchCode(researchCode);
        researchEntity.setResearchName(researchName);
        researchEntity.setResearchDate(researchDate);
        researchEntity.setResearchJson(json);
        researchEntity.setJsonVersion(version);
        researchRepository.saveAndFlush(researchEntity);
        log.info("Save new Research to DB: {}, researchCode: {}", researchName, researchCode);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createRegion(String regionName) {
        var entity = new Region();
        entity.setRegionName(regionName);
        regionRepository.save(entity);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void checkAndCreateRegionResearch(String place, String regionName, String researchCode) {
        var regionId = regionRepository.findIdByRegionName(regionName);
        var researchId = researchRepository.findIdByResearchCode(researchCode);
        if (regionId.isPresent() && researchId.isPresent()) {
            var regionResearchEntity = new RegionResearch();
            var regionResearchPk = new RegionResearch.RegionResearchId();
            regionResearchPk.setRegionId(regionId.get());
            regionResearchPk.setResearchId(researchId.get());
            regionResearchEntity.setId(regionResearchPk);
            regionResearchEntity.setRegionPlace(Integer.parseInt(place));
            regionResearchRepository.save(regionResearchEntity);
        } else {
            log.error("regionId or researchId was not found by regionName {} and researchCode {}",
                    regionName, researchCode);
        }
    }

}