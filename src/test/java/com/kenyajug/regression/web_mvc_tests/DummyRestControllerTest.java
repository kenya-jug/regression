package com.kenyajug.regression.web_mvc_tests;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DummyRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void testHelloEndpoint() throws Exception {
        mockMvc.perform(get("/api/dummy/hello")
                .with(user("xyz@abc.com")))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from DummyRestController!"));
    }

    @Test
    void openApiJson_shouldContainApiMetadata() throws Exception {
        mockMvc.perform(get("/v3/api-docs")
                .with(user("xyz@abc.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Regression"))
                .andExpect(jsonPath("$.info.version").value("v1"))
                .andExpect(jsonPath("$.paths").isNotEmpty());
    }

    @Test
    void openApi_shouldContainEndpoint_forUsers() throws Exception {
        mockMvc.perform(get("/v3/api-docs")
                        .with(user("xyz@abc.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/dummy/hello']").exists())
                .andExpect(jsonPath("$.paths['/api/dummy/hello'].get").exists());
    }

}
