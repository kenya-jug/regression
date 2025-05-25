package com.kenyajug.regression.web_mvc_tests;


import com.kenyajug.regression.entities.AppLog;
import com.kenyajug.regression.services.IIngestionService;
import com.kenyajug.regression.utils.DateTimeUtils;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(LogsIngestionController.class)
class LogsIngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Mock
    IIngestionService ingestionService;

    private AppLog appLog;

    //@BeforeEach
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

    //@Test
    void shouldReturnConflictIfLogExists() throws Exception {

        mockMvc.perform(post("/logs/ingest")
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
                .andExpect(status().isConflict())
                .andExpect(content().string("Log with id test-uuid already exists"));

        verify(ingestionService, never()).saveAppLog(any());
    }

    //@Test
    void shouldCreateLogIfNotExists() throws Exception {

        mockMvc.perform(post("/logs/ingest")
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
}