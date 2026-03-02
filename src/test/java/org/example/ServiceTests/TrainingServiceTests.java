package org.example.ServiceTests;

import com.gym.crm.Repository.TraineeRepository;
import com.gym.crm.Repository.TrainerRepository;
import com.gym.crm.Repository.TrainingRepository;
import com.gym.crm.Repository.TrainingTypeRepository;
import com.gym.crm.dto.TrainingDto;
import com.gym.crm.model.*;
import com.gym.crm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTests {

    @Mock private TraineeRepository traineeRepository;
    @Mock private TrainerRepository trainerRepository;
    @Mock private TrainingTypeRepository trainingTypeRepository;
    @Mock private TrainingRepository trainingRepository;

    @InjectMocks private TrainingService trainingService;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;
    private Training training;

    @BeforeEach
    void setUp() {
        User traineeUser = new User("dato", "jincharadze", "dato.jincharadze", "pass");
        User trainerUser = new User("gio", "jincharadze", "gio.jincharadze", "pass");
        trainingType = new TrainingType("Yoga");
        trainee = new Trainee(traineeUser, LocalDate.of(1990, 1, 1), "zastava");
        trainer = new Trainer(trainerUser, trainingType);
        training = new Training(trainee, trainer, trainingType, "Morning Yoga",
                LocalDate.of(2024, 6, 1), 60L);
        training.setId(1L);
    }

    @Test
    void createTraining_shouldSaveAndReturnTraining() {
        TrainingDto dto = buildDto("dato.jincharadze", "gio.jincharadze", "Yoga",
                "Morning Yoga", LocalDate.of(2024,6,1), 60L);
        when(traineeRepository.findByUsername("dato.jincharadze")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("gio.jincharadze")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByName("Yoga")).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any())).thenReturn(training);

        Training result = trainingService.createTraining(dto);

        assertThat(result.getTrainingName()).isEqualTo("Morning Yoga");
        assertThat(result.getTrainingDuration()).isEqualTo(60L);
        verify(trainingRepository).save(any());
    }

    @Test
    void createTraining_shouldThrowWhenEntitiesNotFound() {
        TrainingDto dto = buildDto("ghost", "gio.jincharadze", "Yoga", null, null, null);
        when(traineeRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> trainingService.createTraining(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ghost");

        dto.setTraineeUsername("dato.jincharadze");
        dto.setTrainerUsername("ghost");
        when(traineeRepository.findByUsername("dato.jincharadze")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> trainingService.createTraining(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ghost");

        dto.setTrainerUsername("gio.jincharadze");
        dto.setTrainingTypeName("InvalidType");
        when(trainerRepository.findByUsername("gio.jincharadze")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByName("InvalidType")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> trainingService.createTraining(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("InvalidType");
    }

    @Test
    void getTraineeTrainings_shouldDelegateToRepository() {
        when(trainingRepository.findTraineeTrainings("dato.jincharadze", null, null, null, null))
                .thenReturn(List.of(training));
        List<Training> result = trainingService.getTraineeTrainings("dato.jincharadze", null, null, null, null);
        assertThat(result).containsExactly(training);
    }

    @Test
    void getTrainerTrainings_shouldDelegateToRepository() {
        when(trainingRepository.findTrainerTrainings("gio.jincharadze", null, null, null))
                .thenReturn(List.of(training));
        List<Training> result = trainingService.getTrainerTrainings("gio.jincharadze", null, null, null);
        assertThat(result).containsExactly(training);
    }

    @Test
    void selectAllTrainings_shouldReturnAll() {
        when(trainingRepository.findAll()).thenReturn(List.of(training));
        List<Training> result = trainingService.selectAllTrainings();
        assertThat(result).containsExactly(training);
    }

    private TrainingDto buildDto(String traineeUsername, String trainerUsername, String trainingTypeName,
                                 String name, LocalDate date, Long duration) {
        TrainingDto dto = new TrainingDto();
        dto.setTraineeUsername(traineeUsername);
        dto.setTrainerUsername(trainerUsername);
        dto.setTrainingTypeName(trainingTypeName);
        dto.setTrainingName(name);
        dto.setTrainingDate(date);
        dto.setTrainingDuration(duration);
        return dto;
    }
}