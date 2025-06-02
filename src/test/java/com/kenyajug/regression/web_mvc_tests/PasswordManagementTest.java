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
import com.kenyajug.regression.controllers.UserController;
import com.kenyajug.regression.entities.User;
import com.kenyajug.regression.repository.UserRepository;
import com.kenyajug.regression.resources.PasswordUpdateResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
public class PasswordManagementTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserController userController;
    @MockitoBean
    private UserRepository userRepository;
    @Test
    @DisplayName("Should launch password reset page successfully")
    public void shouldLaunchPasswordResetPageTest() throws Exception {
        var user = new User("57e8dd9f-90de-4cd9-9696-de85caedeadc","mokky@email.com","pass***","Admin", LocalDateTime.now());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        mockMvc.perform(get("/user/form")
                        .with(
                                user("viuea@admin").roles("ADMIN"))
                                .with(csrf())
                        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user-form"))
                .andExpect(model().attributeExists("password"));
    }
    @Test
    @DisplayName("Should return 302 redirect on password reset page under certain conditions")
    public void shouldThrow302_OnPasswordResetPageTest() throws Exception {
        mockMvc.perform(get("/user/form"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
    @Test
    @DisplayName("Should throw SecurityException under security violation conditions")
    public void shouldThrowSecurityExceptionTest() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(SecurityException.class, () -> userController.launchUserUpdateForm(any(Model.class)));
    }
    @Test
    @DisplayName("Only authenticated admin user can update admin credentials")
    public void shouldThrowForbiddenTest() throws Exception {
        var passwordUpdate = new PasswordUpdateResource("old_pass","new_pass","new_pass");
        mockMvc.perform(post("/admin/update")
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("currentPassword",passwordUpdate.currentPassword())
                        .param("newPassword",passwordUpdate.newPassword())
                        .param("confirmPassword",passwordUpdate.confirmPassword())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
    @Test
    @DisplayName("Throw security exception if default admin account is not present for this update")
    public void shouldAdminFormThrowSecurityExceptionTest()  {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        var passwordUpdate = new PasswordUpdateResource("old_pass","new_pass","new_pass");
        BindingResult bindingResult = new BeanPropertyBindingResult(passwordUpdate, "password");
        assertThrows(SecurityException.class, () -> userController.updateAdminPassword(null,passwordUpdate,bindingResult));
    }
    @Test
    @DisplayName("Should update admin password successfully")
    public void shouldUpdateAdminPasswordTest() throws Exception {
        var user = new User("57e8dd9f-90de-4cd9-9696-de85caedeadc","mokky@email.com","pass***","Admin", LocalDateTime.now());
        var passwordUpdate = new PasswordUpdateResource("old_pass","new_pass","new_pass");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        mockMvc.perform(post("/admin/update")
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("currentPassword",passwordUpdate.currentPassword())
                        .param("newPassword",passwordUpdate.newPassword())
                        .param("confirmPassword",passwordUpdate.confirmPassword())
                        .with(user("superadmin"))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(userRepository, times(1)).updateById(anyString(),any(User.class));
    }
    @Test
    @DisplayName("Should fail if confirm password does not match new password")
    public void shouldFailIfConfirmPasswordsDoesNotMatchTest() {
        var user = new User("57e8dd9f-90de-4cd9-9696-de85caedeadc","mokky@email.com","pass***","Admin", LocalDateTime.now());
        var passwordUpdate = new PasswordUpdateResource("old_pass","new_pass","new_new_pass");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        BindingResult bindingResult = new BeanPropertyBindingResult(passwordUpdate, "password");
        assertThrows(RuntimeException.class, () -> userController.updateAdminPassword(null,passwordUpdate,bindingResult));
        verify(userRepository, times(0)).updateById(anyString(),any(User.class));
    }
    @Test
    @DisplayName("Should not save when password fields are empty")
    public void shouldNotSaveEmptyPasswordsTest() throws Exception {
        var user = new User("57e8dd9f-90de-4cd9-9696-de85caedeadc","mokky@email.com","pass***","Admin", LocalDateTime.now());
        var passwordUpdate = new PasswordUpdateResource("old_pass","new_pass","");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        mockMvc.perform(post("/admin/update")
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("currentPassword",passwordUpdate.currentPassword())
                        .param("newPassword",passwordUpdate.newPassword())
                        .param("confirmPassword",passwordUpdate.confirmPassword())
                        .with(user("superadmin"))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user-form"));
        verify(userRepository, times(0)).updateById(anyString(),any(User.class));
    }
    @Test
    @DisplayName("Should not save when password fields are empty - case 2")
    public void shouldNotSaveEmptyPasswords_Case2_Test() throws Exception {
        var user = new User("57e8dd9f-90de-4cd9-9696-de85caedeadc","mokky@email.com","pass***","Admin", LocalDateTime.now());
        var passwordUpdate = new PasswordUpdateResource("","","");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        mockMvc.perform(post("/admin/update")
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("currentPassword",passwordUpdate.currentPassword())
                        .param("newPassword",passwordUpdate.newPassword())
                        .param("confirmPassword",passwordUpdate.confirmPassword())
                        .with(user("superadmin"))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user-form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("password","currentPassword"))
                .andExpect(model().attributeHasFieldErrors("password","newPassword"))
                .andExpect(model().attributeHasFieldErrors("password","confirmPassword"));
        verify(userRepository, times(0)).updateById(anyString(),any(User.class));
    }
    @Test
    @DisplayName("Should not save when passwords do not match - case 2")
    public void shouldNotSaveMismatchPasswords_Case2_Test() throws Exception {
        var user = new User("57e8dd9f-90de-4cd9-9696-de85caedeadc","mokky@email.com","pass***","Admin", LocalDateTime.now());
        var passwordUpdate = new PasswordUpdateResource("pass***","new_pass***12345","new__new_pass***89091");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        mockMvc.perform(post("/admin/update")
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("currentPassword",passwordUpdate.currentPassword())
                        .param("newPassword",passwordUpdate.newPassword())
                        .param("confirmPassword",passwordUpdate.confirmPassword())
                        .with(user("superadmin"))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user-form"))
                .andExpect(model().attributeExists("password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("password","confirmPassword"));
        verify(userRepository, times(0)).updateById(anyString(),any(User.class));
    }
}
