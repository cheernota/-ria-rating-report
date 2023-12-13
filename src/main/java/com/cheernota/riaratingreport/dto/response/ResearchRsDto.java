package com.cheernota.riaratingreport.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@Value
public class ResearchRsDto {

    private final Long researchId;
    private final String researchName;

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private final LocalDate researchDate;
}
