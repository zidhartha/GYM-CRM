package org.example.ServiceTests;

import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.util.EntityMapper;
import com.gym.crm.util.PasswordGenerator;
import com.gym.crm.util.UsernameGenerator;
import com.gym.crm.dto.authentication.RegistrationResponseDto;
import com.gym.crm.dto.trainee.TraineeCreateDto;
import com.gym.crm.dto.trainee.TraineeProfileDto;
import com.gym.crm.dto.trainee.TraineeUpdateDto;
import com.gym.crm.dto.trainer.TrainerListDto;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.User;
import com.gym.crm.service.TraineeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock private TraineeRepository traineeRepository;
    @Mock private PasswordGenerator passwordGenerator;
    @Mock private UsernameGenerator usernameGenerator;
    @Mock private EntityMapper entityMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private TraineeService traineeService;

    @Test
    void createTrainee_shouldReturnUsernameAndPassword() {
        TraineeCreateDto dto = new TraineeCreateDto("Gio", "Janelidze", null, null);
        when(passwordGenerator.generatePassword()).thenReturn("pass123");
        when(usernameGenerator.generateUsername("Gio", "Janelidze")).thenReturn("gio.janelidze");
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass123");

        User user = new User("Gio", "Janelidze", "gio.janelidze", "encodedPass123");
        Trainee saved = new Trainee(user, null, null);
        when(traineeRepository.save(any())).thenReturn(saved);

        RegistrationResponseDto result = traineeService.createTrainee(dto);

        assertEquals("gio.janelidze", result.getUsername());
        assertEquals("pass123", result.getPassword());
    }

    @Test
    void getTraineeProfile_shouldReturnProfile() {
        User user = new User("Gio", "Janelidze", "gio.janelidze", "pass");
        user.setActive(true);
        Trainee trainee = new Trainee(user, null, null);
        trainee.setTrainers(new ArrayList<>());

        when(traineeRepository.findByUserUsername("gio.janelidze")).thenReturn(Optional.of(trainee));
        when(entityMapper.mapToTrainerListDto(any())).thenReturn(new TrainerListDto(List.of()));

        TraineeProfileDto result = traineeService.getTraineeProfile("gio.janelidze");

        assertEquals("Gio", result.getFirstName());
        assertEquals("Janelidze", result.getLastName());
    }

    @Test
    void getTraineeProfile_shouldThrowWhenNotFound() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.getTraineeProfile("unknown"));
    }

    @Test
    void updateTraineeProfile_shouldUpdateAndReturn() {
        User user = new User("Gio", "Janelidze", "gio.janelidze", "pass");
        Trainee trainee = new Trainee(user, null, null);
        trainee.setTrainers(new ArrayList<>());

        TraineeUpdateDto dto = new TraineeUpdateDto();
        dto.setFirstName("Giorgi");
        dto.setLastName("Janelidze");

        when(traineeRepository.findByUserUsername("gio.janelidze")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any())).thenReturn(trainee);
        when(entityMapper.mapToTrainerListDto(any())).thenReturn(new TrainerListDto(List.of()));

        TraineeProfileDto result = traineeService.updateTraineeProfile(dto, "gio.janelidze");

        assertEquals("Giorgi", result.getFirstName());
    }

    @Test
    void updateTraineeProfile_shouldThrowWhenNotFound() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.updateTraineeProfile(new TraineeUpdateDto(), "unknown"));
    }

    @Test
    void deleteTrainee_shouldCallRepository() {
        traineeService.deleteTrainee("gio.janelidze");
        verify(traineeRepository).deleteByUserUsername("gio.janelidze");
    }

    @Test
    void updateTraineeTrainers_shouldThrowWhenTraineeNotFound() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.updateTraineeTrainers("unknown", List.of("gio.jincharadze")));
    }
}