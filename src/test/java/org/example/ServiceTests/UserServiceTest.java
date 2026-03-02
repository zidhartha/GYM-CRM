package org.example.ServiceTests;
import com.gym.crm.Repository.UserRepository;
import com.gym.crm.dto.LoginRequestDto;
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
        user = new User("John", "Doe", "John.Doe", "encodedPass");
        user.setId(1L);
        user.setActive(true);
    }

    @Test
    void authenticate_shouldPassWithCorrectCredentials() {
        LoginRequestDto credentials = new LoginRequestDto("John.Doe", "rawPass");

        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);

        assertThatNoException().isThrownBy(() -> userService.authenticate(credentials));
    }

    @Test
    void authenticate_shouldThrowWhenUserNotFound() {
        LoginRequestDto credentials = new LoginRequestDto("ghost", "pass");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.authenticate(credentials))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ghost");
    }

    @Test
    void authenticate_shouldThrowWhenPasswordWrong() {
        LoginRequestDto credentials = new LoginRequestDto("John.Doe", "wrongPass");

        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        assertThatThrownBy(() -> userService.authenticate(credentials))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid password");
    }

    @Test
    void updatePassword_shouldEncodeAndSaveNewPassword() {
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("newEncodedPass");

        userService.updatePassword("John.Doe", "newPass");

        assertThat(user.getPassword()).isEqualTo("newEncodedPass");
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updatePassword("ghost", "newPass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ghost");
    }

    @Test
    void updateActiveStatus_shouldToggleFromTrueToFalse() {
        user.setActive(true);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));

        userService.updateActiveStatus("John.Doe");

        assertThat(user.isActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    void updateActiveStatus_shouldToggleFromFalseToTrue() {
        user.setActive(false);
        when(userRepository.findByUsername("John.Doe")).thenReturn(Optional.of(user));

        userService.updateActiveStatus("John.Doe");

        assertThat(user.isActive()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void updateActiveStatus_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateActiveStatus("ghost"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ghost");

        verify(userRepository, never()).save(any());
    }
}