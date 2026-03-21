package org.example.FacadeTest;

import com.gym.crm.dto.*;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.model.*;
import com.gym.crm.service.*;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock private TraineeService traineeService;
    @Mock private TrainerService trainerService;
    @Mock private TrainingService trainingService;
    @Mock private UserService userService;

    @InjectMocks
    private GymFacade gymFacade;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;

    @BeforeEach
    void setUp() {

        TrainingType type = new TrainingType("Yoga");

        trainee = new Trainee(
                new User("John","Doe","John.Doe","pass"),
                LocalDate.of(1990,1,1),
                "Address"
        );

        trainer = new Trainer(
                new User("Jane","Smith","Jane.Smith","pass"),
                type
        );

        training = new Training(
                trainee,
                trainer,
                type,
                "Morning Yoga",
                LocalDate.of(2024,6,1),
                60L
        );
    }

    @Test
    void authenticate_shouldDelegateToUserService() {

        gymFacade.authenticate("John.Doe","pass");

        verify(userService).authenticate(any(LoginRequestDto.class));
    }

    @Test
    void authenticate_shouldThrowWhenCredentialsInvalid() {

        doThrow(new IllegalArgumentException())
                .when(userService).authenticate(any());

        assertThatThrownBy(() ->
                gymFacade.authenticate("John.Doe","wrong"))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void createTrainee_shouldDelegateToService() {

        when(traineeService.createTrainee(any())).thenReturn(trainee);

        Trainee result = gymFacade.createTrainee(
                "John","Doe",
                LocalDate.of(1990,1,1),
                "Address"
        );

        assertThat(result).isEqualTo(trainee);
        verify(traineeService).createTrainee(any());
    }

    @Test
    void selectTrainee_shouldAuthenticateThenReturnTrainee() {

        when(traineeService.getTraineeByUsername("John.Doe"))
                .thenReturn(trainee);

        Trainee result =
                gymFacade.selectTrainee("John.Doe","pass","John.Doe");

        assertThat(result).isEqualTo(trainee);

        verify(userService).authenticate(any());
        verify(traineeService).getTraineeByUsername("John.Doe");
    }

    @Test
    void deleteTrainee_shouldAuthenticateThenDelete() {

        gymFacade.deleteTrainee("John.Doe","pass");

        verify(userService).authenticate(any());
        verify(traineeService).deleteTrainee("John.Doe");
    }

    @Test
    void matchTraineeCredentials_shouldReturnTrueWhenValid() {

        boolean result =
                gymFacade.matchTraineeCredentials("John.Doe","pass");

        assertThat(result).isTrue();
    }


    @Test
    void createTrainer_shouldDelegateToService() {

        when(trainerService.createTrainer(any())).thenReturn(trainer);

        Trainer result =
                gymFacade.createTrainer("Jane","Smith","Yoga");

        assertThat(result).isEqualTo(trainer);
    }

    @Test
    void selectTrainer_shouldAuthenticateThenReturnTrainer() {

        when(trainerService.getTrainerByUsername("Jane.Smith"))
                .thenReturn(Optional.of(trainer));

        Trainer result =
                gymFacade.selectTrainer("Jane.Smith","pass","Jane.Smith");

        assertThat(result).isEqualTo(trainer);
    }

    @Test
    void updateTrainer_shouldAuthenticateThenUpdate() {

        when(trainerService.updateTrainer(any(TrainerUpdateDto.class), eq("Jane.Smith")))
                .thenReturn(trainer);

        Trainer result =
                gymFacade.updateTrainer(
                        "Jane.Smith",
                        "pass",
                        "Jane",
                        "Smith",
                        "Yoga"
                );

        assertThat(result).isEqualTo(trainer);

        verify(userService).authenticate(any(LoginRequestDto.class));
        verify(trainerService).updateTrainer(any(TrainerUpdateDto.class), eq("Jane.Smith"));
    }


    @Test
    void createTraining_shouldAuthenticateAndCreate() {

        when(trainingService.createTraining(any()))
                .thenReturn(training);

        Training result =
                gymFacade.createTraining(
                        "John.Doe",
                        "pass",
                        "Jane.Smith",
                        "Morning Yoga",
                        "Yoga",
                        LocalDate.of(2024,6,1),
                        60L
                );

        assertThat(result).isEqualTo(training);

        verify(userService).authenticate(any());
        verify(trainingService).createTraining(any());
    }

    @Test
    void getTrainerTrainings_shouldReturnTrainings() {

        when(trainingService.getTrainerTrainings(
                eq("Jane.Smith"), any(), any(), any()))
                .thenReturn(List.of(training));

        List<Training> result =
                gymFacade.getTrainerTrainings(
                        "Jane.Smith",
                        "pass",
                        null,
                        null,
                        null
                );

        assertThat(result).containsExactly(training);
    }


    @Test
    void selectAllTrainees_shouldReturnAll() {

        when(traineeService.selectAllTrainees())
                .thenReturn(List.of(trainee));

        assertThat(gymFacade.selectAllTrainees())
                .containsExactly(trainee);
    }

    @Test
    void selectAllTrainers_shouldReturnAll() {

        when(trainerService.selectAllTrainers())
                .thenReturn(List.of(trainer));

        assertThat(gymFacade.selectAllTrainers())
                .containsExactly(trainer);
    }

    @Test
    void selectAllTrainings_shouldReturnAll() {

        when(trainingService.selectAllTrainings())
                .thenReturn(List.of(training));

        assertThat(gymFacade.selectAllTrainings())
                .containsExactly(training);
    }
}