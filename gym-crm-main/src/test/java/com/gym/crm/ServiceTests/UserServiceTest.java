package com.gym.crm.ServiceTests;

import com.gym.crm.repository.UserRepository;
import com.gym.crm.dto.authentication.LoginRequestDto;
import com.gym.crm.exceptions.AccessDeniedException;
import com.gym.crm.model.User;
import com.gym.crm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("John", "Doe", "John.Doe", "rawPass");
        user.setId(1L);
        user.setActive(true);
    }

    @Test
    void authenticate_shouldPassWithCorrectCredentials() {
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPass", "rawPass")).thenReturn(true);

        assertThatNoException()
                .isThrownBy(() -> userService.authenticate(new LoginRequestDto("John.Doe", "rawPass")));
    }

    @Test
    void authenticate_shouldThrowWhenCredentialsInvalid() {
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "rawPass")).thenReturn(false);

        assertThatThrownBy(() -> userService.authenticate(new LoginRequestDto("John.Doe", "wrongPass")))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("John.Doe");
    }

    @Test
    void updatePassword_shouldSetNewPassword() {
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        userService.updatePassword("John.Doe", "newPass");

        assertThat(user.getPassword()).isEqualTo("encodedNewPass");
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updatePassword("ghost", "newPass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateActiveStatus_shouldToggleFromTrueToFalse() {
        user.setActive(true);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));

        userService.deactivateUser("John.Doe");

        assertThat(user.isActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    void updateActiveStatus_shouldToggleFromFalseToTrue() {
        user.setActive(false);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));

        userService.activateUser("John.Doe");

        assertThat(user.isActive()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void updateActiveStatus_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.activateUser("ghost"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ghost");

        verify(userRepository, never()).save(any());
    }

    @Test
    void activateUser_shouldSetActiveToTrue() {
        user.setActive(false);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));

        userService.activateUser("John.Doe");

        assertThat(user.isActive()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void activateUser_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.activateUser("ghost"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ghost");

        verify(userRepository, never()).save(any());
    }

    @Test
    void deactivateUser_shouldSetActiveToFalse() {
        user.setActive(true);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));

        userService.deactivateUser("John.Doe");

        assertThat(user.isActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    void deactivateUser_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deactivateUser("ghost"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ghost");

        verify(userRepository, never()).save(any());
    }
}