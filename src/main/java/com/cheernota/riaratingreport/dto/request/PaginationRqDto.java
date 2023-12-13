package com.cheernota.riaratingreport.dto.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRqDto {

    @JsonSetter(nulls = Nulls.SKIP)
    @Schema(defaultValue = "0")
    private Integer pageNumber = 0;

    @NotNull
    @Schema(defaultValue = "20")
    private Integer pageSize;
}
