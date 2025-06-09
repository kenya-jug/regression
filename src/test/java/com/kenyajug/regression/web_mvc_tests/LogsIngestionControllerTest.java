package com.kenyajug.regression.web_mvc_tests;


import com.kenyajug.regression.controllers.LogsIngestionController;
import com.kenyajug.regression.entities.AppLog;
import com.kenyajug.regression.resources.ApplicationResource;
import com.kenyajug.regression.resources.DatasourceResource;
import com.kenyajug.regression.resources.LogResource;
import com.kenyajug.regression.services.IngestionService;
import com.kenyajug.regression.services.RetrievalService;
import com.kenyajug.regression.utils.DateTimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LogsIngestionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RetrievalService retrievalService;

    @MockitoBean
    IngestionService ingestionService;

    @MockitoBean
    private AppLog appLog;

    @BeforeEach
    void setUp() {
        appLog = new AppLog(
                "test-uuid",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC") ,
                "ERROR",
                "app-123",
                "",
                "Test message"
                // add other fields as needed
        );
    }

    @Test
    void shouldReturnConflictIfLogExists() throws Exception {
        var datetime = DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC");
        AppLog applog = new AppLog(
                "test-uuid",
                datetime,
                "ERROR",
                "app-123",
                "",
                "Test message"
        );
        when(retrievalService.findAppLogById("test-uuid")).thenReturn(Optional.of(applog));
        mockMvc.perform(post("/ingest")
                        .with(httpBasic("admin@regression.com", "admin123")) // Assuming basic auth is used
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "uuid": "test-uuid",
                            "timestamp": "%s",
                            "severity": "ERROR",
                            "applicationId": "app-123",
                            "message": "Test message"
                        }
                        """.formatted(datetime)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Log with id test-uuid already exists"));

        verify(ingestionService, never()).saveAppLog(any());
    }

    @Test
    void shouldCreateLogIfNotExists() throws Exception {

        mockMvc.perform(post("/ingest")
                        .with(httpBasic("admin@regression.com", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "uuid": "test-uuid",
                        "timestamp": "2025-05-08T12:00:00",
                        "severity": "ERROR",
                        "applicationId": "app-123",
                        "message": "Test message"
                    }
                """))
                .andExpect(status().isCreated());

        verify(ingestionService, times(1)).saveAppLog(any());
    }


    @Test
    void shouldReturnUnauthorizedIfNoCredentials() throws Exception {
        mockMvc.perform(post("/ingest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "uuid": "test-uuid",
                        "timestamp": "2025-05-08T12:00:00",
                        "severity": "ERROR",
                        "applicationId": "app-123",
                        "message": "Test message"
                    }
                """))
                .andExpect(status().isUnauthorized());
    }
}