package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareItGateway.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper;
    private final WebApplicationContext context;

    @MockBean
    private UserClient userClient;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @SneakyThrows
    void getUser_whenValidId_thenReturnOk() {
        when(userClient.getUser(1L))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "name", "John Doe",
                        "email", "john@test.com"
                )));

        mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@test.com"));

        verify(userClient).getUser(1L);
    }

    @Test
    @SneakyThrows
    void getUser_whenInvalidId_thenReturnBadRequest() {
        Assertions.assertAll(
                () -> mockMvc.perform(get("/users/{userId}", -1L))
                        .andExpect(status().isBadRequest()),
                () -> mockMvc.perform(get("/users/{userId}", 0L))
                        .andExpect(status().isBadRequest())
        );

        verifyNoInteractions(userClient);
    }

    @Test
    @SneakyThrows
    void createUser_whenValid_thenReturnOk() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("John Doe")
                .email("john@test.com")
                .build();

        when(userClient.createUser(any(UserCreateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "name", dto.getName(),
                        "email", dto.getEmail()
                )));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@test.com"));

        verify(userClient).createUser(any(UserCreateDto.class));
    }

    @Test
    @SneakyThrows
    void createUser_whenInvalidData_thenReturnBadRequest() {
        UserCreateDto invalidDto = UserCreateDto.builder()
                .name("")
                .email("invalid-email")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }

    @Test
    @SneakyThrows
    void updateUser_whenValid_thenReturnOk() {
        UserUpdateDto dto = UserUpdateDto.builder()
                .name("Jane Doe")
                .build();

        when(userClient.updateUser(eq(1L), any(UserUpdateDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "id", 1L,
                        "name", dto.getName(),
                        "email", "john@test.com"
                )));

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"));

        verify(userClient).updateUser(eq(1L), any(UserUpdateDto.class));
    }

    @Test
    @SneakyThrows
    void deleteUser_whenValid_thenReturnOk() {
        when(userClient.deleteUser(1L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(1L);
    }
}