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
import com.kenyajug.regression.controllers.ApplicationController;
import com.kenyajug.regression.entities.Application;
import com.kenyajug.regression.entities.User;
import com.kenyajug.regression.repository.ApplicationsRepository;
import com.kenyajug.regression.repository.UserRepository;
import com.kenyajug.regression.resources.ApplicationResource;
import com.kenyajug.regression.security.SecurityHelper;
import com.kenyajug.regression.security.SecurityUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:application-noliquibase-test.properties")
public class ApplicationUITest {
    @MockitoBean
    private ApplicationsRepository applicationsRepository;
    @MockitoBean
    private SecurityHelper securityHelper;
    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private ApplicationController applicationController;
    @Autowired
    private MockMvc mockMvc;
    @DisplayName("Data source application registration is restricted to authorized users only ")
    @Test
    public void shouldThrowUnauthorizedAccessErrorApplicationFormTest() throws Exception {
        mockMvc.perform(get("/add/application"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
    @Test
    @DisplayName("Should launch source application form successfully")
    public void shouldLaunchSourceApplicationFormTest() throws Exception {
        mockMvc.perform(get("/add/application")
                        .with(user("maxbracker@regression.com").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("application-form"))
                .andExpect(model().attributeExists("application"));
    }
    @Test
    @DisplayName("Should save application successfully")
    public void shouldSaveApplicationTest() throws Exception {
        var securityUser = new SecurityUser("vladimir@ru.com","$2a$10$D1r0ghp70r...aC7pS3Ozi3IM", List.of());
        var user = new User("2ae29319-c7cb-4964-a7ab-4905715f5105",
                "vladimir@ru.com",
                "$2a$10$D1r0ghp70r...aC7pS3Ozi3IM",
                "",
                LocalDateTime.now());
        when(securityHelper.findAuthenticatedUser()).thenReturn(securityUser);
        when(userRepository.findByUsername(securityUser.getUsername())).thenReturn(Optional.of(user));
        mockMvc.perform(post("/add/application")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("uuid","a2f39251-d821-4dc0-93eb-5b582d54535e")
                .param("name","name")
                .param("runtimeEnvironment","runtimeEnvironment")
                .with(user("maxwell-min").roles("ADMIN"))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications"));
    }
    @Test
    @DisplayName("Should validate form input correctly")
    public void shouldValidateFormInputTest() throws Exception {
        mockMvc.perform(post("/add/application")
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("uuid","")
                        .param("name","")
                        .param("runtimeEnvironment","")
                        .with(user("maxwell-min").roles("ADMIN"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("application-form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("application","uuid"))
                .andExpect(model().attributeHasFieldErrors("application","name"))
                .andExpect(model().attributeHasFieldErrors("application","runtimeEnvironment"))
                .andExpect(model().hasErrors());
    }
    @Test
    @DisplayName("On form submission, application record should be saved in an sqlite database")
    public void shouldCallRepositorySaveTest() throws Exception {
        var application = new ApplicationResource("a2f39251-d821-4dc0-93eb-5b582d54535e","Gimp","Linux");
        var securityUser = new SecurityUser("vladimir@ru.com","$2a$10$D1r0ghp70r...aC7pS3Ozi3IM", List.of());
        var user = new User("2ae29319-c7cb-4964-a7ab-4905715f5105",
                "vladimir@ru.com",
                "$2a$10$D1r0ghp70r...aC7pS3Ozi3IM",
                "",
                LocalDateTime.now());
        when(securityHelper.findAuthenticatedUser()).thenReturn(securityUser);
        when(userRepository.findByUsername(securityUser.getUsername())).thenReturn(Optional.of(user));
        mockMvc.perform(post("/add/application")
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("uuid",application.uuid())
                        .param("name",application.name())
                        .param("runtimeEnvironment",application.runtimeEnvironment())
                        .with(user("maxwell-min").roles("ADMIN"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications"));
        verify(applicationsRepository, times(1)).save(any(Application.class));
    }
    @Test
    @DisplayName("Should list all applications successfully")
    public void shouldListApplicationsTest() throws Exception {
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
        mockMvc.perform(get("/applications")
                .with(user("vladimir@ru.com"))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("applications-list"))
                .andExpect(model().attributeExists("apps"))
                .andExpect(model().attribute("apps", apps));
    }
    @Test
    @DisplayName("Throw security exception if principle user is not persisted")
    public void shouldThrowSecurityExceptionTest()  {
        var securityUser = new SecurityUser("vladimir@ru.com","$2a$10$D1r0ghp70r...aC7pS3Ozi3IM", List.of());
        when(securityHelper.findAuthenticatedUser()).thenReturn(securityUser);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        var applicationResource = new ApplicationResource("2097f3c8-e08d-4499-b536-753e9f4aded3","Gimp","JVM");
        BindingResult bindingResult = new BeanPropertyBindingResult(applicationResource, "application");
        assertThrows(SecurityException.class, () -> applicationController.saveNewApplication(applicationResource,bindingResult));
    }
    @Test
    @DisplayName("Throw security exception if principle user is not persisted")
    public void shouldThrowSecurityException_Case2_Test()  {
        var securityUser = new SecurityUser("vladimir@ru.com","$2a$10$D1r0ghp70r...aC7pS3Ozi3IM", List.of());
        when(securityHelper.findAuthenticatedUser()).thenReturn(securityUser);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(SecurityException.class, () -> applicationController.listApplications(null));
    }
}
