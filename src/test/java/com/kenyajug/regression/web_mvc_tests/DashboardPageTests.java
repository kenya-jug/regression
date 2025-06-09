package com.kenyajug.regression.web_mvc_tests;
/*
 * MIT License
 *
 * Copyright (c) 2025 Kenya JUG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import com.kenyajug.regression.controllers.DashboardController;
import com.kenyajug.regression.entities.Application;
import com.kenyajug.regression.entities.LogsDataSource;
import com.kenyajug.regression.entities.User;
import com.kenyajug.regression.repository.ApplicationsRepository;
import com.kenyajug.regression.repository.LogsDataSourceRepository;
import com.kenyajug.regression.repository.UserRepository;
import com.kenyajug.regression.security.SecurityHelper;
import com.kenyajug.regression.security.SecurityUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:application-noliquibase-test.properties")
public class DashboardPageTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DashboardController dashboardController;
    @MockitoBean
    private SecurityHelper securityHelper;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private ApplicationsRepository applicationsRepository;
    @MockitoBean
    private LogsDataSourceRepository logsDataSourceRepository;
    @Test
    @DisplayName("Home page should not be publicly accessible")
    public void shouldLoadHomePage_UnauthorizedTest() throws Exception{
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
    @Test
    @DisplayName("Should load home page successfully")
    public void shouldLoadHomePageTest() throws Exception{
        var securityUser = new SecurityUser("vladimir@ru.com","$2a$10$D1r0ghp70r...aC7pS3Ozi3IM", List.of());
        var user = new User("2ae29319-c7cb-4964-a7ab-4905715f5105",
                "vladimir@ru.com",
                "$2a$10$D1r0ghp70r...aC7pS3Ozi3IM",
                "",
                LocalDateTime.now());
        when(securityHelper.findAuthenticatedUser()).thenReturn(securityUser);
        when(userRepository.findByUsername(securityUser.getUsername())).thenReturn(Optional.of(user));
        mockMvc.perform(get("/")
                        .with(user("jetLee@regress").roles("ADMIN"))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Ensure applications are listed in the home page")
    public void shouldListApplicationsInHomePageTest() throws Exception{
        var timestamp = LocalDateTime.of(2000,11,8,17,34,50);
        var apps = List.of(
                new Application("2097f3c8-e08d-4499-b536-753e9f4aded3","Gimp","1.0","JVM","",timestamp),
                new Application("2097f3c8-e08d-4499-b536-753e9f4aded3","Chrome","1.0","V8","",timestamp),
                new Application("2097f3c8-e08d-4499-b536-753e9f4aded3","Xcode","1.0","macOs","",timestamp)
        );
        var securityUser = new SecurityUser("vladimir@ru.com","$2a$10$D1r0ghp70r...aC7pS3Ozi3IM", List.of());
        var user = new User("2ae29319-c7cb-4964-a7ab-4905715f5105",
                "vladimir@ru.com",
                "$2a$10$D1r0ghp70r...aC7pS3Ozi3IM",
                "",
                LocalDateTime.now());
        when(securityHelper.findAuthenticatedUser()).thenReturn(securityUser);
        when(userRepository.findByUsername(securityUser.getUsername())).thenReturn(Optional.of(user));
        when(applicationsRepository.findByOwner(any())).thenReturn(apps);
        mockMvc.perform(get("/")
                        .with(user("jetLee@regress").roles("ADMIN"))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("apps"))
                .andExpect(model().attribute("apps",apps))
                .andExpect(model().attributeExists("logs"))
                .andExpect(model().attributeExists("logChartData"))
                .andExpect(view().name("dashboard"));
    }
    @Test
    @DisplayName("Throw security exception auth user does not match persisted users list")
    public void shouldNotLoadDashboard_withEmptyUserTest()  {
        var securityUser = new SecurityUser("vladimir@ru.com","$2a$10$D1r0ghp70r...aC7pS3Ozi3IM", List.of());
        when(securityHelper.findAuthenticatedUser()).thenReturn(securityUser);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(SecurityException.class, () -> dashboardController.dashboard(null));
    }
    @Test
    @DisplayName("Should list all data sources successfully")
    public void shouldListDataSourcesTest() throws Exception {
        var datasourceList = List.of(
                new LogsDataSource(
                        "UUID1",
                        "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                        "local",
                        "App_UUID1",
                        LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                        "/var/log/mysql/error.log"
                ),
                new LogsDataSource(
                        "UUID2",
                        "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                        "local",
                        "App_UUID1",
                        LocalDateTime.of(2001, 11, 5, 21, 15, 0),
                        "/var/log/tomcat/catalina.out"
                )
        );
        var securityUser = new SecurityUser("vladimir@ru.com","$2a$10$D1r0ghp70r...aC7pS3Ozi3IM", List.of());
        var user = new User("2ae29319-c7cb-4964-a7ab-4905715f5105",
                "vladimir@ru.com",
                "$2a$10$D1r0ghp70r...aC7pS3Ozi3IM",
                "",
                LocalDateTime.now());
        when(securityHelper.findAuthenticatedUser()).thenReturn(securityUser);
        when(userRepository.findByUsername(securityUser.getUsername())).thenReturn(Optional.of(user));
        when(logsDataSourceRepository.findAll()).thenReturn(datasourceList);
        mockMvc.perform(get("/")
                        .with(user("jetLee@regress").roles("ADMIN"))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("logsDatasourceList"))
                .andExpect(model().attributeExists("logChartData"))
                .andExpect(model().attribute("logsDatasourceList",datasourceList))
                .andExpect(view().name("dashboard"));
    }
}
