package org.example.ServiceTests;

import com.gym.crm.Repository.TraineeRepository;
import com.gym.crm.Repository.TrainerRepository;
import com.gym.crm.Repository.TrainingRepository;
import com.gym.crm.Repository.TrainingTypeRepository;
import com.gym.crm.dto.training.TrainingCreateDto;
import com.gym.crm.model.*;
import com.gym.crm.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock private TraineeRepository traineeRepository;
    @Mock private TrainerRepository trainerRepository;
    @Mock private TrainingTypeRepository trainingTypeRepository;
    @Mock private TrainingRepository trainingRepository;

    @InjectMocks private TrainingService trainingService;

    private Trainee mockTrainee() {
        User user = new User("John", "Doe", "john.doe", "pass");
        return new Trainee(user, null, null);
    }

    private Trainer mockTrainer() {
        TrainingType type = new TrainingType();
        type.setName("Yoga");
        User user = new User("Jane", "Smith", "jane.smith", "pass");
        return new Trainer(user, type);
    }

    private TrainingType mockTrainingType() {
        TrainingType type = new TrainingType();
        type.setName("Yoga");
        return type;
    }

    @Test
    void createTraining_shouldSaveAndReturn() {
        TrainingCreateDto dto = TrainingCreateDto.builder()
                .traineeUsername("john.doe")
                .trainerUsername("jane.smith")
                .trainingTypeName("Yoga")
                .trainingName("Morning Yoga")
                .trainingDate(LocalDate.now())
                .trainingDuration(60L)
                .build();

        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(mockTrainee()));
        when(trainerRepository.findByUserUsername("jane.smith")).thenReturn(Optional.of(mockTrainer()));
        when(trainingTypeRepository.findByName("Yoga")).thenReturn(Optional.of(mockTrainingType()));
        when(trainingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Training result = trainingService.createTraining(dto);

        assertEquals("Morning Yoga", result.getTrainingName());
        verify(trainingRepository).save(any());
    }

    @Test
    void createTraining_shouldThrowWhenTraineeNotFound() {
        TrainingCreateDto dto = TrainingCreateDto.builder()
                .traineeUsername("unknown")
                .trainerUsername("jane.smith")
                .trainingTypeName("Yoga")
                .trainingName("Session")
                .trainingDate(LocalDate.now())
                .trainingDuration(60L)
                .build();

        when(traineeRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(dto));
    }

    @Test
    void createTraining_shouldThrowWhenTrainerNotFound() {
        TrainingCreateDto dto = TrainingCreateDto.builder()
                .traineeUsername("john.doe")
                .trainerUsername("unknown")
                .trainingTypeName("Yoga")
                .trainingName("Session")
                .trainingDate(LocalDate.now())
                .trainingDuration(60L)
                .build();

        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(mockTrainee()));
        when(trainerRepository.findByUserUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(dto));
    }

    @Test
    void createTraining_shouldThrowWhenTrainingTypeNotFound() {
        TrainingCreateDto dto = TrainingCreateDto.builder()
                .traineeUsername("john.doe")
                .trainerUsername("jane.smith")
                .trainingTypeName("Unknown")
                .trainingName("Session")
                .trainingDate(LocalDate.now())
                .trainingDuration(60L)
                .build();

        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(Optional.of(mockTrainee()));
        when(trainerRepository.findByUserUsername("jane.smith")).thenReturn(Optional.of(mockTrainer()));
        when(trainingTypeRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(dto));
    }

    @Test
    void getTraineeTrainings_shouldReturnList() {
        when(trainingRepository.findTraineeTrainings("john.doe", null, null, null, null))
                .thenReturn(List.of());

        var result = trainingService.getTraineeTrainings("john.doe", null, null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTrainerTrainings_shouldReturnList() {
        when(trainingRepository.findTrainerTrainings("jane.smith", null, null, null))
                .thenReturn(List.of());

        var result = trainingService.getTrainerTrainings("jane.smith", null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}