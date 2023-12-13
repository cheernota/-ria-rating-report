package com.cheernota.riaratingreport.controller;

import com.cheernota.riaratingreport.dto.response.RegionRsDto;
import com.cheernota.riaratingreport.service.ResearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/region")
@Tag(name = "Region endpoints")
public class RegionController {

    private final ResearchService researchService;

    @GetMapping(value = "/list", produces = "application/json;charset=utf-8")
    @Operation(summary = "Get list of regions with their average rating")
    public ResponseEntity<List<RegionRsDto>> getRegionList() {
        return ResponseEntity.ok(researchService.getRegionList());
    }
}
