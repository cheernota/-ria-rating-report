package com.cheernota.riaratingreport.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ResearchResultRsDto {

    private final List<ResearchRsDto> list;
    private final long totalCount;
}
