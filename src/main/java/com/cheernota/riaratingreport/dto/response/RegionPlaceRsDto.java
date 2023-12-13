package com.cheernota.riaratingreport.dto.response;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class RegionPlaceRsDto {

    private final String regionName;
    private final Integer place;
}
