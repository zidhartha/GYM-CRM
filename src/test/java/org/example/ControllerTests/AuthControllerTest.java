package org.example.ControllerTests;

import com.gym.crm.controller.AuthController;
import com.gym.crm.dto.authentication.ChangePasswordRequestDto;
import com.gym.crm.dto.authentication.LoginRequestDto;
import com.gym.crm.exceptions.AccessDeniedException;
import com.gym.crm.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock UserService userService;
    @InjectMocks AuthController authController;

    @Test
    void login_shouldReturn200() {
        ResponseEntity<Void> response = authController.login("dato.jincharadze", "pass");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).authenticate("dato.jincharadze", "pass");
    }

    @Test
    void login_shouldThrowWhenInvalidCredentials() {
        doThrow(new AccessDeniedException("Invalid credentials"))
                .when(userService).authenticate("dato.jincharadze", "wrongpass");

        assertThrows(AccessDeniedException.class,
                () -> authController.login("dato.jincharadze", "wrongpass"));
    }

    @Test
    void changePassword_shouldReturn200() {
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto("dato.jincharadze", "oldpass", "newpass");

        ResponseEntity<Void> response = authController.changePassword(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).updatePassword("dato.jincharadze", "newpass");
    }

    @Test
    void changePassword_shouldThrowWhenAuthFails() {
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto("dato.jincharadze", "wrongpass", "newpass");
        doThrow(new AccessDeniedException("Invalid credentials"))
                .when(userService).authenticate(any(LoginRequestDto.class));

        assertThrows(AccessDeniedException.class,
                () -> authController.changePassword(dto));
    }
}
