package org.example.ServiceTests;
import com.gym.crm.Repository.TraineeRepository;
import com.gym.crm.Util.PasswordGenerator;
import com.gym.crm.Util.UsernameGenerator;
import com.gym.crm.dto.trainee.TraineeCreateDto;
import com.gym.crm.dto.trainee.TraineeProfileDto;
import com.gym.crm.dto.trainee.TraineeUpdateDto;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.User;
import com.gym.crm.service.TraineeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks private TraineeService traineeService;

    @Test
    void createTrainee_shouldReturnUsernameAndPassword() {
        TraineeCreateDto dto = new TraineeCreateDto("John", "Doe", null, null);
        when(passwordGenerator.generatePassword()).thenReturn("pass123");
        when(usernameGenerator.generateUsername("John", "Doe")).thenReturn("John.Doe");

        User user = new User("John", "Doe", "John.Doe", "pass123");
        Trainee saved = new Trainee(user, null, null);
        when(traineeRepository.save(any())).thenReturn(saved);

        var result = traineeService.createTrainee(dto);

        assertEquals("John.Doe", result.get("username"));
        assertEquals("pass123", result.get("password"));
    }

    @Test
    void getTraineeProfile_shouldReturnProfile() {
        User user = new User("John", "Doe", "john.doe", "pass");
        user.setActive(true);
        Trainee trainee = new Trainee(user, null, null);

        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(traineeRepository.findTraineeTrainers("john.doe")).thenReturn(List.of());

        TraineeProfileDto result = traineeService.getTraineeProfile("john.doe");

        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void getTraineeProfile_shouldThrowWhenNotFound() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.getTraineeProfile("unknown"));
    }

    @Test
    void updateTraineeProfile_shouldUpdateAndReturn() {
        User user = new User("John", "Doe", "john.doe", "pass");
        Trainee trainee = new Trainee(user, null, null);
        TraineeUpdateDto dto = new TraineeUpdateDto();
        dto.setFirstName("Johnny");
        dto.setLastName("Doe");

        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any())).thenReturn(trainee);
        when(traineeRepository.findTraineeTrainers("john.doe")).thenReturn(List.of());

        TraineeProfileDto result = traineeService.updateTraineeProfile(dto, "john.doe");

        assertEquals("Johnny", result.getFirstName());
    }

    @Test
    void updateTraineeProfile_shouldThrowWhenNotFound() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.updateTraineeProfile(new TraineeUpdateDto(), "unknown"));
    }

    @Test
    void deleteTrainee_shouldCallRepository() {
        traineeService.deleteTrainee("john.doe");
        verify(traineeRepository).deleteByUserUsername("john.doe");
    }

    @Test
    void updateTraineeTrainers_shouldThrowWhenTraineeNotFound() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.updateTraineeTrainers("unknown", List.of("trainer1")));
    }
}