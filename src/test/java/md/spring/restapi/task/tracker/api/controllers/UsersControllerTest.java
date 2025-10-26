package md.spring.restapi.task.tracker.api.controllers;

import md.spring.restapi.task.tracker.api.dto.UserDto;
import md.spring.restapi.task.tracker.api.services.UsersService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService userService;

    @Test
    @WithMockUser(authorities="ADMIN")
    void fetchUsers() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("User 1")
                .build();
        List<UserDto> userDtos = Collections.singletonList(userDto);
        when(userService.fetchUsers()).thenReturn(userDtos);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("User 1"));
    }

    @Test
    @WithMockUser(authorities="ADMIN")
    void createUser() throws Exception {
        String userName = "User 1";
        String password = "password";
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username(userName)
                .build();
        when(userService.createUser(userName, password)).thenReturn(userDto);

        mockMvc.perform(put("/api/admin/user")
                        .param("user_name", userName)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(userName));
    }

    @Test
    @WithMockUser(authorities="ADMIN")
    void updateUser() throws Exception {
        Long userId = 1L;
        String userName = "Updated User Name";
        String password = "password";
        UserDto updatedUserDto = UserDto.builder()
                .id(userId)
                .username(userName)
                .build();
        when(userService.editUser(userId, Optional.of(userName), Optional.of(password))).thenReturn(updatedUserDto);

        mockMvc.perform(patch("/api/admin/users/{user_id}", userId)
                        .param("user_name", userName)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(userName));
    }

    @Test
    @WithMockUser(authorities="ADMIN")
    void deleteUser() throws Exception {
        Long userId = 1L;
        mockMvc.perform(delete("/api/admin/users/{user_id}", userId))
                .andExpect(status().isOk());
    }
}