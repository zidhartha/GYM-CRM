package com.gym.crm.FacadeTest;

import com.gym.crm.dto.authentication.LoginRequestDto;
import com.gym.crm.dto.authentication.RegistrationResponseDto;
import com.gym.crm.dto.trainee.TraineeCreateDto;
import com.gym.crm.dto.trainee.TraineeProfileDto;
import com.gym.crm.dto.trainee.TraineeUpdateDto;
import com.gym.crm.dto.trainer.TrainerCreateDto;
import com.gym.crm.dto.trainer.TrainerListDto;
import com.gym.crm.dto.trainer.TrainerProfileDto;
import com.gym.crm.dto.trainer.TrainerUpdateDto;
import com.gym.crm.dto.training.TrainingCreateDto;
import com.gym.crm.dto.trainingType.TrainingTypeListDto;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock private TraineeService traineeService;
    @Mock private TrainerService trainerService;
    @Mock private TrainingService trainingService;
    @Mock private TrainingTypeService trainingTypeService;
    @Mock private UserService userService;

    @InjectMocks private GymFacade gymFacade;

    @Test
    void authenticate_shouldDelegateToUserService() {
        LoginRequestDto loginRequestDto = new LoginRequestDto("gio.janelidze","password");
        gymFacade.authenticate(loginRequestDto);
        verify(userService).authenticate(loginRequestDto);
    }

    @Test
    void updatePassword_shouldDelegateToUserService() {
        gymFacade.updatePassword("gio.janelidze", "newpass");
        verify(userService).updatePassword("gio.janelidze", "newpass");
    }

    @Test
    void activateUser_shouldDelegateToUserService() {
        gymFacade.activateUser("gio.janelidze");
        verify(userService).activateUser("gio.janelidze");
    }

    @Test
    void deactivateUser_shouldDelegateToUserService() {
        gymFacade.deactivateUser("gio.janelidze");
        verify(userService).deactivateUser("gio.janelidze");
    }

    @Test
    void createTrainee_shouldReturnRegistrationResponse() {
        TraineeCreateDto dto = new TraineeCreateDto("Gio", "Janelidze", null, null);
        RegistrationResponseDto expected = RegistrationResponseDto.builder()
                .username("gio.janelidze")
                .password("pass123")
                .build();
        when(traineeService.createTrainee(dto)).thenReturn(expected);

        RegistrationResponseDto result = gymFacade.createTrainee(dto);

        assertEquals("gio.janelidze", result.getUsername());
        assertEquals("pass123", result.getPassword());
        verify(traineeService).createTrainee(dto);
    }

    @Test
    void getTraineeProfile_shouldReturnProfile() {
        TraineeProfileDto expected = TraineeProfileDto.builder()
                .firstName("Gio")
                .lastName("Janelidze")
                .isActive(true)
                .trainers(new TrainerListDto(List.of()))
                .build();
        when(traineeService.getTraineeProfile("gio.janelidze")).thenReturn(expected);

        TraineeProfileDto result = gymFacade.getTraineeProfile("gio.janelidze");

        assertEquals(expected, result);
        verify(traineeService).getTraineeProfile("gio.janelidze");
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
        TraineeProfileDto expected = TraineeProfileDto.builder()
                .firstName("Gio")
                .lastName("Janelidze")
                .isActive(true)
                .trainers(new TrainerListDto(List.of()))
                .build();
        when(traineeService.updateTraineeProfile(dto, "gio.janelidze")).thenReturn(expected);

        TraineeProfileDto result = gymFacade.updateTraineeProfile(dto, "gio.janelidze");

        assertEquals(expected, result);
        verify(traineeService).updateTraineeProfile(dto, "gio.janelidze");
    }

    @Test
    void deleteTrainee_shouldDelegateToTraineeService() {
        gymFacade.deleteTrainee("gio.janelidze");
        verify(traineeService).deleteTrainee("gio.janelidze");
    }

    @Test
    void updateTraineeTrainers_shouldReturnUpdatedTrainerList() {
        List<String> trainerUsernames = List.of("trainer1", "trainer2");
        TrainerListDto expected = new TrainerListDto(List.of());
        when(traineeService.updateTraineeTrainers("gio.janelidze", trainerUsernames)).thenReturn(expected);

        TrainerListDto result = gymFacade.updateTraineeTrainers("gio.janelidze", trainerUsernames);

        assertEquals(expected, result);
        verify(traineeService).updateTraineeTrainers("gio.janelidze", trainerUsernames);
    }

    @Test
    void createTrainer_shouldReturnRegistrationResponse() {
        TrainerCreateDto dto = new TrainerCreateDto("Gio", "Jincharadze", "Yoga");
        RegistrationResponseDto expected = RegistrationResponseDto.builder()
                .username("gio.jincharadze")
                .password("pass456")
                .build();
        when(trainerService.createTrainer(dto)).thenReturn(expected);

        RegistrationResponseDto result = gymFacade.createTrainer(dto);

        assertEquals("gio.jincharadze", result.getUsername());
        assertEquals("pass456", result.getPassword());
        verify(trainerService).createTrainer(dto);
    }

    @Test
    void getTrainerProfile_shouldReturnProfile() {
        TrainerProfileDto expected = TrainerProfileDto.builder()
                .username("gio.jincharadze")
                .firstName("Gio")
                .lastName("Jincharadze")
                .specialization("Yoga")
                .isActive(true)
                .build();
        when(trainerService.getTrainerByUsername("gio.jincharadze")).thenReturn(expected);

        TrainerProfileDto result = gymFacade.getTrainerProfile("gio.jincharadze");

        assertEquals(expected, result);
        verify(trainerService).getTrainerByUsername("gio.jincharadze");
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
        TrainerProfileDto expected = TrainerProfileDto.builder()
                .username("gio.jincharadze")
                .firstName("Gio")
                .lastName("Jincharadze")
                .specialization("Yoga")
                .isActive(true)
                .build();
        when(trainerService.updateTrainer(dto, "gio.jincharadze")).thenReturn(expected);

        TrainerProfileDto result = gymFacade.updateTrainer(dto, "gio.jincharadze");

        assertEquals(expected, result);
        verify(trainerService).updateTrainer(dto, "gio.jincharadze");
    }

    @Test
    void getUnassignedTrainers_shouldReturnTrainerList() {
        TrainerListDto expected = new TrainerListDto(List.of());
        when(trainerService.getUnassignedTrainers("gio.janelidze")).thenReturn(expected);

        TrainerListDto result = gymFacade.getUnassignedTrainers("gio.janelidze");

        assertEquals(expected, result);
        verify(trainerService).getUnassignedTrainers("gio.janelidze");
    }

    @Test
    void createTraining_shouldDelegateToTrainingService() {
        TrainingCreateDto dto = new TrainingCreateDto();
        gymFacade.createTraining(dto);
        verify(trainingService).createTraining(dto);
    }

    @Test
    void getTraineeTrainings_shouldReturnList() {
        when(trainingService.getTraineeTrainings("gio.janelidze", null, null, null, null))
                .thenReturn(List.of());

        var result = gymFacade.getTraineeTrainings("gio.janelidze", null, null, null, null);

        assertNotNull(result);
        verify(trainingService).getTraineeTrainings("gio.janelidze", null, null, null, null);
    }

    @Test
    void getTrainerTrainings_shouldReturnList() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        when(trainingService.getTrainerTrainings("gio.jincharadze", from, to, null))
                .thenReturn(List.of());

        var result = gymFacade.getTrainerTrainings("gio.jincharadze", from, to, null);

        assertNotNull(result);
        verify(trainingService).getTrainerTrainings("gio.jincharadze", from, to, null);
    }

    @Test
    void getAllTrainingTypes_shouldReturnList() {
        TrainingTypeListDto expected = new TrainingTypeListDto(List.of());
        when(trainingTypeService.getAllTrainingTypes()).thenReturn(expected);

        TrainingTypeListDto result = gymFacade.getAllTrainingTypes();

        assertEquals(expected, result);
        verify(trainingTypeService).getAllTrainingTypes();
    }
}