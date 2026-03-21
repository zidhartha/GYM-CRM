package org.example.FacadeTest;
import com.gym.crm.dto.trainee.TraineeCreateDto;
import com.gym.crm.dto.trainee.TraineeProfileDto;
import com.gym.crm.dto.trainee.TraineeUpdateDto;
import com.gym.crm.dto.trainer.TrainerCreateDto;
import com.gym.crm.dto.trainer.TrainerListDto;
import com.gym.crm.dto.trainer.TrainerProfileDto;
import com.gym.crm.dto.trainer.TrainerUpdateDto;
import com.gym.crm.dto.training.TrainingCreateDto;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock private TraineeService traineeService;
    @Mock private TrainerService trainerService;
    @Mock private TrainingService trainingService;
    @Mock private UserService userService;

    @InjectMocks private GymFacade gymFacade;

    @Test
    void authenticate_shouldDelegateToUserService() {
        gymFacade.authenticate("john.doe", "password");
        verify(userService).authenticate("john.doe", "password");
    }

    @Test
    void assertIdentity_shouldDelegateToUserService() {
        gymFacade.assertIdentity("john.doe", "john.doe");
        verify(userService).assertIdentity("john.doe", "john.doe");
    }

    @Test
    void updatePassword_shouldDelegateToUserService() {
        gymFacade.updatePassword("john.doe", "newpass");
        verify(userService).updatePassword("john.doe", "newpass");
    }

    @Test
    void activateUser_shouldDelegateToUserService() {
        gymFacade.activateUser("john.doe");
        verify(userService).activateUser("john.doe");
    }

    @Test
    void deactivateUser_shouldDelegateToUserService() {
        gymFacade.deactivateUser("john.doe");
        verify(userService).deactivateUser("john.doe");
    }

    @Test
    void createTrainee_shouldReturnUsernameAndPassword() {
        TraineeCreateDto dto = new TraineeCreateDto("John", "Doe", null, null);
        when(traineeService.createTrainee(dto))
                .thenReturn(Map.of("username", "john.doe", "password", "pass123"));

        Map<String, String> result = gymFacade.createTrainee(dto);

        assertEquals("john.doe", result.get("username"));
        assertEquals("pass123", result.get("password"));
        verify(traineeService).createTrainee(dto);
    }

    @Test
    void getTraineeProfile_shouldReturnProfile() {
        TraineeProfileDto expected = new TraineeProfileDto(
                "John", "Doe", null, null, true, new TrainerListDto(List.of()));
        when(traineeService.getTraineeProfile("john.doe")).thenReturn(expected);

        TraineeProfileDto result = gymFacade.getTraineeProfile("john.doe");

        assertEquals(expected, result);
        verify(traineeService).getTraineeProfile("john.doe");
    }

    @Test
    void getTraineeProfile_shouldThrowWhenNotFound() {
        when(traineeService.getTraineeProfile("unknown"))
                .thenThrow(new IllegalArgumentException("Trainee not found: unknown"));

        assertThrows(IllegalArgumentException.class,
                () -> gymFacade.getTraineeProfile("unknown"));
    }

    @Test
    void updateTraineeProfile_shouldReturnUpdatedProfile() {
        TraineeUpdateDto dto = new TraineeUpdateDto();
        TraineeProfileDto expected = new TraineeProfileDto(
                "John", "Smith", null, null, true, new TrainerListDto(List.of()));
        when(traineeService.updateTraineeProfile(dto, "john.doe")).thenReturn(expected);

        TraineeProfileDto result = gymFacade.updateTraineeProfile(dto, "john.doe");

        assertEquals(expected, result);
        verify(traineeService).updateTraineeProfile(dto, "john.doe");
    }

    @Test
    void deleteTrainee_shouldDelegateToTraineeService() {
        gymFacade.deleteTrainee("john.doe");
        verify(traineeService).deleteTrainee("john.doe");
    }

    @Test
    void updateTraineeTrainers_shouldReturnUpdatedTrainerList() {
        List<String> trainerUsernames = List.of("trainer1", "trainer2");
        TrainerListDto expected = new TrainerListDto(List.of());
        when(traineeService.updateTraineeTrainers("john.doe", trainerUsernames))
                .thenReturn(expected);

        TrainerListDto result = gymFacade.updateTraineeTrainers("john.doe", trainerUsernames);

        assertEquals(expected, result);
        verify(traineeService).updateTraineeTrainers("john.doe", trainerUsernames);
    }

    @Test
    void createTrainer_shouldReturnUsernameAndPassword() {
        TrainerCreateDto dto = new TrainerCreateDto("Jane", "Smith", "Yoga");
        when(trainerService.createTrainer(dto))
                .thenReturn(Map.of("username", "jane.smith", "password", "pass456"));

        Map<String, String> result = gymFacade.createTrainer(dto);

        assertEquals("jane.smith", result.get("username"));
        verify(trainerService).createTrainer(dto);
    }

    @Test
    void getTrainerProfile_shouldReturnProfile() {
        TrainerProfileDto expected = new TrainerProfileDto(
                "jane.smith", "Jane", "Smith", "Yoga", true, null);
        when(trainerService.getTrainerByUsername("jane.smith")).thenReturn(expected);

        TrainerProfileDto result = gymFacade.getTrainerProfile("jane.smith");

        assertEquals(expected, result);
        verify(trainerService).getTrainerByUsername("jane.smith");
    }

    @Test
    void getTrainerProfile_shouldThrowWhenNotFound() {
        when(trainerService.getTrainerByUsername("unknown"))
                .thenThrow(new IllegalArgumentException("Trainer not found: unknown"));

        assertThrows(IllegalArgumentException.class,
                () -> gymFacade.getTrainerProfile("unknown"));
    }

    @Test
    void updateTrainer_shouldReturnUpdatedProfile() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        TrainerProfileDto expected = new TrainerProfileDto(
                "jane.smith", "Jane", "Updated", "Yoga", true, null);
        when(trainerService.updateTrainer(dto, "jane.smith")).thenReturn(expected);

        TrainerProfileDto result = gymFacade.updateTrainer(dto, "jane.smith");

        assertEquals(expected, result);
        verify(trainerService).updateTrainer(dto, "jane.smith");
    }

    @Test
    void getUnassignedTrainers_shouldReturnTrainerList() {
        TrainerListDto expected = new TrainerListDto(List.of());
        when(trainerService.getUnassignedTrainers("john.doe")).thenReturn(expected);

        TrainerListDto result = gymFacade.getUnassignedTrainers("john.doe");

        assertEquals(expected, result);
        verify(trainerService).getUnassignedTrainers("john.doe");
    }

    @Test
    void createTraining_shouldDelegateToTrainingService() {
        TrainingCreateDto dto = new TrainingCreateDto();
        gymFacade.createTraining(dto);
        verify(trainingService).createTraining(dto);
    }

    @Test
    void getTraineeTrainings_shouldReturnList() {
        when(trainingService.getTraineeTrainings("john.doe", null, null, null, null))
                .thenReturn(List.of());

        var result = gymFacade.getTraineeTrainings("john.doe", null, null, null, null);

        assertNotNull(result);
        verify(trainingService).getTraineeTrainings("john.doe", null, null, null, null);
    }

    @Test
    void getTrainerTrainings_shouldReturnList() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        when(trainingService.getTrainerTrainings("jane.smith", from, to, null))
                .thenReturn(List.of());

        var result = gymFacade.getTrainerTrainings("jane.smith", from, to, null);

        assertNotNull(result);
        verify(trainingService).getTrainerTrainings("jane.smith", from, to, null);
    }
}