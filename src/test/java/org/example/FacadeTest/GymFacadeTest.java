package org.example.FacadeTest;

import com.gym.crm.dto.TraineeDto;
import com.gym.crm.dto.TraineeUpdateDto;
import com.gym.crm.dto.TrainerDto;
import com.gym.crm.dto.TrainerUpdateDto;
import com.gym.crm.dto.TrainingDto;
import com.gym.crm.dto.LoginRequestDto;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.UserService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock private TraineeService traineeService;
    @Mock private TrainerService trainerService;
    @Mock private TrainingService trainingService;
    @Mock private UserService userService;

    @InjectMocks private GymFacade gymFacade;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType("Yoga");
        trainingType.setId(1L);

        User traineeUser = new User("John", "Doe", "John.Doe", "pass");
        trainee = new Trainee(traineeUser, LocalDate.of(1990, 1, 1), "123 Main St");
        trainee.setId(1L);

        User trainerUser = new User("Jane", "Smith", "Jane.Smith", "pass");
        trainer = new Trainer(trainerUser, trainingType);
        trainer.setId(2L);

        training = new Training(trainee, trainer, trainingType, "Morning Yoga",
                LocalDate.of(2024, 6, 1), 60L);
        training.setId(3L);
    }

    // AUTHENTICATE

    @Test
    void authenticate_shouldDelegateToUserService() {
        gymFacade.authenticate("John.Doe", "pass");

        verify(userService).authenticate(any(LoginRequestDto.class));
    }

    @Test
    void authenticate_shouldThrowWhenUserServiceThrows() {
        doThrow(new IllegalArgumentException("Invalid password"))
                .when(userService).authenticate(any(LoginRequestDto.class));

        assertThatThrownBy(() -> gymFacade.authenticate("John.Doe", "wrongPass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid password");
    }

    // CREATE TRAINEE

    @Test
    void createTrainee_shouldDelegateToTraineeService() {
        when(traineeService.createTrainee(any(TraineeDto.class))).thenReturn(trainee);

        Trainee result = gymFacade.createTrainee("John", "Doe",
                LocalDate.of(1990, 1, 1), "123 Main St");

        assertThat(result).isNotNull();
        assertThat(result.getUser().getUsername()).isEqualTo("John.Doe");
        verify(traineeService).createTrainee(any(TraineeDto.class));
        verifyNoInteractions(userService);
    }

    // SELECT TRAINEE

    @Test
    void selectTrainee_shouldAuthenticateAndReturnTrainee() {
        when(traineeService.getTraineeByUsername("John.Doe")).thenReturn(trainee);

        Trainee result = gymFacade.selectTrainee("John.Doe", "pass", "John.Doe");

        assertThat(result).isNotNull();
        assertThat(result.getUser().getUsername()).isEqualTo("John.Doe");
        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(traineeService).getTraineeByUsername("John.Doe");
    }

    @Test
    void selectTrainee_shouldThrowWhenAuthFails() {
        doThrow(new IllegalArgumentException("Invalid password"))
                .when(userService).authenticate(any(LoginRequestDto.class));

        assertThatThrownBy(() -> gymFacade.selectTrainee("John.Doe", "wrongPass", "John.Doe"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(traineeService, never()).getTraineeByUsername(any());
    }

    // UPDATE TRAINEE

    @Test
    void updateTrainee_shouldAuthenticateAndUpdate() {
        when(traineeService.updateTraineeProfile(any(TraineeUpdateDto.class), eq("John.Doe")))
                .thenReturn(trainee);

        Trainee result = gymFacade.updateTrainee("John.Doe", "pass",
                "New Address", LocalDate.of(1990, 1, 1));

        assertThat(result).isNotNull();
        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(traineeService).updateTraineeProfile(any(TraineeUpdateDto.class), eq("John.Doe"));
    }

    @Test
    void updateTrainee_shouldThrowWhenAuthFails() {
        doThrow(new IllegalArgumentException("Invalid password"))
                .when(userService).authenticate(any(LoginRequestDto.class));

        assertThatThrownBy(() ->
                gymFacade.updateTrainee("John.Doe", "wrongPass", "Address", LocalDate.now()))
                .isInstanceOf(IllegalArgumentException.class);

        verify(traineeService, never()).updateTraineeProfile(any(), any());
    }

    // DELETE TRAINEE

    @Test
    void deleteTrainee_shouldAuthenticateAndDelete() {
        gymFacade.deleteTrainee("John.Doe", "pass");

        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(traineeService).deleteTrainee("John.Doe");
    }

    @Test
    void deleteTrainee_shouldThrowWhenAuthFails() {
        doThrow(new IllegalArgumentException("Invalid password"))
                .when(userService).authenticate(any(LoginRequestDto.class));

        assertThatThrownBy(() -> gymFacade.deleteTrainee("John.Doe", "wrongPass"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(traineeService, never()).deleteTrainee(any());
    }

    // CHANGE TRAINEE PASSWORD

    @Test
    void changeTraineePassword_shouldAuthenticateWithOldPasswordThenUpdate() {
        gymFacade.changeTraineePassword("John.Doe", "oldPass", "newPass");

        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(userService).updatePassword("John.Doe", "newPass");
    }

    @Test
    void changeTraineePassword_shouldThrowWhenOldPasswordWrong() {
        doThrow(new IllegalArgumentException("Invalid password"))
                .when(userService).authenticate(any(LoginRequestDto.class));

        assertThatThrownBy(() ->
                gymFacade.changeTraineePassword("John.Doe", "wrongOld", "newPass"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userService, never()).updatePassword(any(), any());
    }

    // TOGGLE TRAINEE ACTIVE

    @Test
    void toggleTraineeActive_shouldAuthenticateAndToggle() {
        gymFacade.toggleTraineeActive("John.Doe", "pass");

        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(userService).updateActiveStatus("John.Doe");
    }

    // MATCH TRAINEE CREDENTIALS

    @Test
    void matchTraineeCredentials_shouldReturnTrueWhenValid() {
        boolean result = gymFacade.matchTraineeCredentials("John.Doe", "pass");

        assertThat(result).isTrue();
        verify(userService).authenticate(any(LoginRequestDto.class));
    }

    @Test
    void matchTraineeCredentials_shouldReturnFalseWhenInvalid() {
        doThrow(new IllegalArgumentException("Invalid password"))
                .when(userService).authenticate(any(LoginRequestDto.class));

        boolean result = gymFacade.matchTraineeCredentials("John.Doe", "wrongPass");

        assertThat(result).isFalse();
    }

    // GET TRAINEE TRAININGS

    @Test
    void getTraineeTrainings_shouldAuthenticateAndReturnList() {
        when(trainingService.getTraineeTrainings(eq("John.Doe"), any(), any(), any(), any()))
                .thenReturn(List.of(training));

        List<Training> result = gymFacade.getTraineeTrainings(
                "John.Doe", "pass", null, null, null, null);

        assertThat(result).hasSize(1).containsExactly(training);
        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(trainingService).getTraineeTrainings(eq("John.Doe"), any(), any(), any(), any());
    }

    // GET UNASSIGNED TRAINERS

    @Test
    void getUnassignedTrainers_shouldAuthenticateAndReturnList() {
        when(trainerService.getUnassignedTrainers("John.Doe")).thenReturn(List.of(trainer));

        List<Trainer> result = gymFacade.getUnassignedTrainers("John.Doe", "pass");

        assertThat(result).hasSize(1).containsExactly(trainer);
        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(trainerService).getUnassignedTrainers("John.Doe");
    }

    // UPDATE TRAINEE TRAINERS

    @Test
    void updateTraineeTrainers_shouldAuthenticateAndUpdate() {
        when(traineeService.updateTraineeTrainers("John.Doe", List.of("Jane.Smith")))
                .thenReturn(trainee);

        Trainee result = gymFacade.updateTraineeTrainers(
                "John.Doe", "pass", List.of("Jane.Smith"));

        assertThat(result).isNotNull();
        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(traineeService).updateTraineeTrainers("John.Doe", List.of("Jane.Smith"));
    }

    // CREATE TRAINER

    @Test
    void createTrainer_shouldDelegateToTrainerService() {
        when(trainerService.createTrainer(any(TrainerDto.class))).thenReturn(trainer);

        Trainer result = gymFacade.createTrainer("Jane", "Smith", "Yoga");

        assertThat(result).isNotNull();
        assertThat(result.getUser().getUsername()).isEqualTo("Jane.Smith");
        verify(trainerService).createTrainer(any(TrainerDto.class));
        verifyNoInteractions(userService);
    }

    // SELECT TRAINER

    @Test
    void selectTrainer_shouldAuthenticateAndReturnTrainer() {
        when(trainerService.getTrainerByUsername("Jane.Smith")).thenReturn(Optional.of(trainer));

        Trainer result = gymFacade.selectTrainer("Jane.Smith", "pass", "Jane.Smith");

        assertThat(result).isNotNull();
        assertThat(result.getUser().getUsername()).isEqualTo("Jane.Smith");
        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(trainerService).getTrainerByUsername("Jane.Smith");
    }

    @Test
    void selectTrainer_shouldThrowWhenTrainerNotFound() {
        when(trainerService.getTrainerByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gymFacade.selectTrainer("Jane.Smith", "pass", "ghost"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ghost");
    }

    // UPDATE TRAINER

    @Test
    void updateTrainer_shouldAuthenticateAndUpdate() {
        when(trainerService.updateTrainer(any(TrainerUpdateDto.class), eq("Jane.Smith")))
                .thenReturn(trainer);

        Trainer result = gymFacade.updateTrainer("Jane.Smith", "pass", "Pilates");

        assertThat(result).isNotNull();
        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(trainerService).updateTrainer(any(TrainerUpdateDto.class), eq("Jane.Smith"));
    }

    // CHANGE TRAINER PASSWORD

    @Test
    void changeTrainerPassword_shouldAuthenticateWithOldPasswordThenUpdate() {
        gymFacade.changeTrainerPassword("Jane.Smith", "oldPass", "newPass");

        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(userService).updatePassword("Jane.Smith", "newPass");
    }

    @Test
    void changeTrainerPassword_shouldThrowWhenOldPasswordWrong() {
        doThrow(new IllegalArgumentException("Invalid password"))
                .when(userService).authenticate(any(LoginRequestDto.class));

        assertThatThrownBy(() ->
                gymFacade.changeTrainerPassword("Jane.Smith", "wrongOld", "newPass"))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userService, never()).updatePassword(any(), any());
    }

    // TOGGLE TRAINER ACTIVE

    @Test
    void toggleTrainerActive_shouldAuthenticateAndToggle() {
        gymFacade.toggleTrainerActive("Jane.Smith", "pass");

        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(userService).updateActiveStatus("Jane.Smith");
    }

    // MATCH TRAINER CREDENTIALS

    @Test
    void matchTrainerCredentials_shouldReturnTrueWhenValid() {
        boolean result = gymFacade.matchTrainerCredentials("Jane.Smith", "pass");

        assertThat(result).isTrue();
    }

    @Test
    void matchTrainerCredentials_shouldReturnFalseWhenInvalid() {
        doThrow(new IllegalArgumentException("Invalid password"))
                .when(userService).authenticate(any(LoginRequestDto.class));

        boolean result = gymFacade.matchTrainerCredentials("Jane.Smith", "wrongPass");

        assertThat(result).isFalse();
    }

    // GET TRAINER TRAININGS

    @Test
    void getTrainerTrainings_shouldAuthenticateAndReturnList() {
        when(trainingService.getTrainerTrainings(eq("Jane.Smith"), any(), any(), any()))
                .thenReturn(List.of(training));

        List<Training> result = gymFacade.getTrainerTrainings(
                "Jane.Smith", "pass", null, null, null);

        assertThat(result).hasSize(1).containsExactly(training);
        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(trainingService).getTrainerTrainings(eq("Jane.Smith"), any(), any(), any());
    }

    // CREATE TRAINING

    @Test
    void createTraining_shouldAuthenticateTraineeAndCreate() {
        when(trainingService.createTraining(any(TrainingDto.class))).thenReturn(training);

        Training result = gymFacade.createTraining(
                "John.Doe", "pass", "Jane.Smith",
                "Morning Yoga", "Yoga",
                LocalDate.of(2024, 6, 1), 60L);

        assertThat(result).isNotNull();
        assertThat(result.getTrainingName()).isEqualTo("Morning Yoga");
        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(trainingService).createTraining(any(TrainingDto.class));
    }

    @Test
    void createTraining_shouldThrowWhenAuthFails() {
        doThrow(new IllegalArgumentException("Invalid password"))
                .when(userService).authenticate(any(LoginRequestDto.class));

        assertThatThrownBy(() -> gymFacade.createTraining(
                "John.Doe", "wrongPass", "Jane.Smith",
                "Morning Yoga", "Yoga",
                LocalDate.of(2024, 6, 1), 60L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(trainingService, never()).createTraining(any());
    }

    // SELECT ALL

    @Test
    void selectAllTrainees_shouldReturnAll() {
        when(traineeService.selectAllTrainees()).thenReturn(List.of(trainee));

        List<Trainee> result = gymFacade.selectAllTrainees();

        assertThat(result).hasSize(1).containsExactly(trainee);
    }

    @Test
    void selectAllTrainers_shouldReturnAll() {
        when(trainerService.selectAllTrainers()).thenReturn(List.of(trainer));

        List<Trainer> result = gymFacade.selectAllTrainers();

        assertThat(result).hasSize(1).containsExactly(trainer);
    }

    @Test
    void selectAllTrainings_shouldReturnAll() {
        when(trainingService.selectAllTrainings()).thenReturn(List.of(training));

        List<Training> result = gymFacade.selectAllTrainings();

        assertThat(result).hasSize(1).containsExactly(training);
    }
}