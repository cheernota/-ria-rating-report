package com.cheernota.riaratingreport.dto.request;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResearchListRqDto {

    @Valid
    private ResearchFilterRqDto filter;

    @Valid
    private PaginationRqDto pagination;
}
