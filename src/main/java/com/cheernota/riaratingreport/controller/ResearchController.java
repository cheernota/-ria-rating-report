package com.cheernota.riaratingreport.controller;

import static com.cheernota.riaratingreport.service.RequestService.getPageable;

import com.cheernota.riaratingreport.dto.request.ResearchFilterRqDto;
import com.cheernota.riaratingreport.dto.request.ResearchListRqDto;
import com.cheernota.riaratingreport.dto.response.RegionPlaceRsDto;
import com.cheernota.riaratingreport.dto.response.ResearchResultRsDto;
import com.cheernota.riaratingreport.service.ReportService;
import com.cheernota.riaratingreport.service.ResearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/research")
@Tag(name = "Research endpoints")
public class ResearchController {

    private final ResearchService researchService;
    private final ReportService reportService;

    @PostMapping(value = "/list", produces = "application/json;charset=utf-8")
    @Operation(summary = "Get list of researches")
    public ResponseEntity<ResearchResultRsDto> getResearchList(@RequestBody @Valid ResearchListRqDto rqDto) {
        return ResponseEntity.ok(researchService.getResearchListByFilter(rqDto.getFilter(),
                getPageable(rqDto.getPagination())));
    }

    @PostMapping("/report")
    @Operation(summary = "Get XLS report with researches result")
    public ResponseEntity<byte[]> getReport(
            @RequestBody @Valid ResearchFilterRqDto filterRqDto,
            HttpServletResponse response
    ) {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=ria-rating-report%s.xls", LocalDate.now()));
        return ResponseEntity.ok(reportService.getResearchReportByFilter(filterRqDto));
    }

    @GetMapping(produces = "application/json;charset=utf-8")
    @Operation(summary = "Get list of regions with places for single research")
    public ResponseEntity<List<RegionPlaceRsDto>> getResultList(
            @RequestParam(name = "researchId") @NotNull @Positive Integer researchId
    ) {
        return ResponseEntity.ok(researchService.getResearchResultListById(researchId));
    }
}
