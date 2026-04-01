package org.example.ControllerTests;

import com.gym.crm.controller.AuthController;
import com.gym.crm.dto.authentication.ChangePasswordRequestDto;
import com.gym.crm.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock UserService userService;
    @InjectMocks AuthController authController;

    @Test
    void changePassword_shouldReturn200() {
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto( "newpass");
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("dato.jincharadze");
        when(authentication.getPrincipal()).thenReturn(userDetails);

        ResponseEntity<Void> response = authController.changePassword(dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).updatePassword("dato.jincharadze", "newpass");
    }
}