package com.cheernota.riaratingreport.dto.response;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class RegionRsDto {

    private final String regionName;
    private final Double averagePlace;
}
