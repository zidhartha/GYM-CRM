package org.example.ServiceTests;

import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.repository.TrainingTypeRepository;
import com.gym.crm.util.EntityMapper;
import com.gym.crm.util.PasswordGenerator;
import com.gym.crm.util.UsernameGenerator;
import com.gym.crm.dto.authentication.RegistrationResponseDto;
import com.gym.crm.dto.trainee.TraineeListDto;
import com.gym.crm.dto.trainer.TrainerCreateDto;
import com.gym.crm.dto.trainer.TrainerListDto;
import com.gym.crm.dto.trainer.TrainerProfileDto;
import com.gym.crm.dto.trainer.TrainerUpdateDto;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private TrainingTypeRepository trainingTypeRepository;
    @Mock private PasswordGenerator passwordGenerator;
    @Mock private UsernameGenerator usernameGenerator;
    @Mock private EntityMapper entityMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private TrainerService trainerService;

    @Test
    void createTrainer_shouldReturnUsernameAndPassword() {
        TrainerCreateDto dto = new TrainerCreateDto("Jane", "Smith", "Yoga");
        when(passwordGenerator.generatePassword()).thenReturn("pass456");
        when(usernameGenerator.generateUsername("Jane", "Smith")).thenReturn("Jane.Smith");
        when(passwordEncoder.encode("pass456")).thenReturn("encodedPass456");

        TrainingType type = new TrainingType();
        type.setName("Yoga");
        when(trainingTypeRepository.findByName("Yoga")).thenReturn(Optional.of(type));

        User user = new User("Jane", "Smith", "Jane.Smith", "encodedPass456");
        Trainer saved = new Trainer(user, type);
        when(trainerRepository.save(any())).thenReturn(saved);

        RegistrationResponseDto result = trainerService.createTrainer(dto);

        assertEquals("Jane.Smith", result.getUsername());
        assertEquals("pass456", result.getPassword());
    }

    @Test
    void createTrainer_shouldThrowWhenTrainingTypeInvalid() {
        TrainerCreateDto dto = new TrainerCreateDto("Jane", "Smith", "InvalidType");
        when(passwordGenerator.generatePassword()).thenReturn("pass456");
        when(usernameGenerator.generateUsername("Jane", "Smith")).thenReturn("Jane.Smith");
        when(trainingTypeRepository.findByName("InvalidType")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.createTrainer(dto));
    }

    @Test
    void getTrainerByUsername_shouldReturnProfile() {
        TrainingType type = new TrainingType();
        type.setName("Yoga");
        User user = new User("Jane", "Smith", "jane.smith", "pass");
        user.setActive(true);
        Trainer trainer = new Trainer(user, type);
        trainer.setTrainees(new HashSet<>());

        when(trainerRepository.findByUserUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(entityMapper.mapToTraineeListDto(any())).thenReturn(new TraineeListDto(List.of()));

        TrainerProfileDto result = trainerService.getTrainerByUsername("jane.smith");

        assertEquals("Jane", result.getFirstName());
        assertEquals("Yoga", result.getSpecialization());
    }

    @Test
    void getTrainerByUsername_shouldThrowWhenNotFound() {
        when(trainerRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.getTrainerByUsername("unknown"));
    }

    @Test
    void updateTrainer_shouldUpdateAndReturn() {
        TrainingType type = new TrainingType();
        type.setName("Yoga");
        User user = new User("Jane", "Smith", "jane.smith", "pass");
        Trainer trainer = new Trainer(user, type);
        trainer.setTrainees(new HashSet<>());

        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setFirstName("Janet");
        dto.setLastName("Smith");
        dto.setIsActive(true);

        when(trainerRepository.findByUserUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any())).thenReturn(trainer);
        when(entityMapper.mapToTraineeListDto(any())).thenReturn(new TraineeListDto(List.of()));

        TrainerProfileDto result = trainerService.updateTrainer(dto, "jane.smith");

        assertEquals("Janet", result.getFirstName());
    }

    @Test
    void updateTrainer_shouldThrowWhenNotFound() {
        when(trainerRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.updateTrainer(new TrainerUpdateDto(), "unknown"));
    }

    @Test
    void getUnassignedTrainers_shouldReturnTrainerList() {
        when(trainerRepository.findTrainersNotAssignedToTrainee("gio.janelidze"))
                .thenReturn(List.of());
        when(entityMapper.mapToTrainerListDto(any()))
                .thenReturn(new TrainerListDto(List.of()));

        TrainerListDto result = trainerService.getUnassignedTrainers("gio.janelidze");

        assertNotNull(result);
        verify(trainerRepository).findTrainersNotAssignedToTrainee("gio.janelidze");
    }
}