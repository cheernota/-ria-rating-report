package com.cheernota.riaratingreport.controller;

import com.cheernota.riaratingreport.service.IndexService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/indexation")
@Tag(name = "Region endpoints")
public class IndexServiceController {

    private final IndexService indexService;

    @GetMapping(value = "/start")
    @Operation(summary = "Manual start of articles indexation")
    public ResponseEntity<String> start() {
        log.info("Got manual request to start indexation");
        CompletableFuture.runAsync(indexService::indexResearches)
                .exceptionally(ex -> {
                    log.error("There was an exception during manual indexation: ", ex);
                    return null;
                });
        return ResponseEntity.ok("Sent manual request to start indexation");
    }
}
