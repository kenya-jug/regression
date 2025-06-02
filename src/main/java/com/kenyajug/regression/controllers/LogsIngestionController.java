package com.kenyajug.regression.controllers;

import com.kenyajug.regression.entities.AppLog;
import com.kenyajug.regression.services.IngestionService;
import com.kenyajug.regression.services.RetrievalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class LogsIngestionController {
    private final RetrievalService retrievalService;
    private final IngestionService ingestionService;

    public LogsIngestionController(RetrievalService retrievalService, IngestionService ingestionService) {
        this.retrievalService = retrievalService;
        this.ingestionService = ingestionService;
    }


    @PostMapping("/logs/ingest")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','datasource')")
    public ResponseEntity<?> ingestLogs(@Valid @RequestBody AppLog appLog) {
        var optionalAppLog = retrievalService.findAppLogById(appLog.uuid());
        if (optionalAppLog.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Log with id " + appLog.uuid() + " already exists");
        } else {
            ingestionService.saveAppLog(appLog);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }
}
