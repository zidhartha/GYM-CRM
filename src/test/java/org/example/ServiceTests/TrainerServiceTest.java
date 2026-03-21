package org.example.ServiceTests;

import com.gym.crm.Repository.TrainerRepository;
import com.gym.crm.Repository.TrainingTypeRepository;
import com.gym.crm.Util.PasswordGenerator;
import com.gym.crm.Util.UsernameGenerator;
import com.gym.crm.dto.trainer.TrainerCreateDto;
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

    @InjectMocks private TrainerService trainerService;

    @Test
    void createTrainer_shouldReturnUsernameAndPassword() {
        TrainerCreateDto dto = new TrainerCreateDto("Jane", "Smith", "Yoga");
        when(passwordGenerator.generatePassword()).thenReturn("pass456");
        when(usernameGenerator.generateUsername("Jane", "Smith")).thenReturn("Jane.Smith");

        TrainingType type = new TrainingType();
        type.setName("Yoga");
        when(trainingTypeRepository.findByName("Yoga")).thenReturn(Optional.of(type));

        User user = new User("Jane", "Smith", "Jane.Smith", "pass456");
        Trainer saved = new Trainer(user, type);
        when(trainerRepository.save(any())).thenReturn(saved);

        var result = trainerService.createTrainer(dto);

        assertEquals("Jane.Smith", result.get("username"));
        assertEquals("pass456", result.get("password"));
    }

    @Test
    void createTrainer_shouldThrowWhenTrainingTypeInvalid() {
        TrainerCreateDto dto = new TrainerCreateDto("Jane", "Smith", "InvalidType");
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

        when(trainerRepository.findByUserUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(trainerRepository.findTrainerTrainees("jane.smith")).thenReturn(List.of());

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
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setFirstName("Janet");
        dto.setLastName("Smith");

        when(trainerRepository.findByUserUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any())).thenReturn(trainer);
        when(trainerRepository.findTrainerTrainees("jane.smith")).thenReturn(List.of());

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
        when(trainerRepository.findTrainersNotAssignedToTrainee("john.doe"))
                .thenReturn(List.of());

        var result = trainerService.getUnassignedTrainers("john.doe");

        assertNotNull(result);
        verify(trainerRepository).findTrainersNotAssignedToTrainee("john.doe");
    }
}