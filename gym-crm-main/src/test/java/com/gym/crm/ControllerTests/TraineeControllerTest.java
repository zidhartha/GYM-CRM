package com.gym.crm.ControllerTests;

import com.gym.crm.controller.TraineeController;
import com.gym.crm.dto.authentication.ActivationDto;
import com.gym.crm.dto.authentication.RegistrationResponseDto;
import com.gym.crm.dto.trainee.TraineeCreateDto;
import com.gym.crm.dto.trainee.TraineeProfileDto;
import com.gym.crm.dto.trainee.TraineeUpdateDto;
import com.gym.crm.dto.trainer.TrainerListDto;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    @Mock TraineeService traineeService;
    @Mock UserService userService;
    @InjectMocks TraineeController traineeController;

    @Test
    void createTrainee_shouldReturn201() {
        TraineeCreateDto dto = new TraineeCreateDto("Dato", "Jincharadze", null, null);
        RegistrationResponseDto expected = RegistrationResponseDto.builder()
                .username("dato.jincharadze")
                .password("pass")
                .build();
        when(traineeService.createTrainee(dto)).thenReturn(expected);

        ResponseEntity<RegistrationResponseDto> response = traineeController.createTrainee(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("dato.jincharadze", response.getBody().getUsername());
    }

    @Test
    void getTraineeProfile_shouldReturn200() {
        TraineeProfileDto profile = TraineeProfileDto.builder()
                .firstName("Dato")
                .lastName("Jincharadze")
                .isActive(true)
                .trainers(new TrainerListDto(List.of()))
                .build();
        when(traineeService.getTraineeProfile("dato.jincharadze")).thenReturn(profile);

        ResponseEntity<TraineeProfileDto> response = traineeController.getTraineeProfile("dato.jincharadze");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dato", response.getBody().getFirstName());
    }

    @Test
    void updateTraineeProfile_shouldReturn200() {
        TraineeUpdateDto dto = new TraineeUpdateDto();
        TraineeProfileDto profile = TraineeProfileDto.builder()
                .firstName("Dato")
                .lastName("Jincharadze")
                .isActive(true)
                .trainers(new TrainerListDto(List.of()))
                .build();
        when(traineeService.updateTraineeProfile(dto, "dato.jincharadze")).thenReturn(profile);

        ResponseEntity<TraineeProfileDto> response = traineeController.updateTraineeProfile("dato.jincharadze", dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteTrainee_shouldReturn200() {
        ResponseEntity<Void> response = traineeController.deleteTraineeProfile("dato.jincharadze");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(traineeService).deleteTrainee("dato.jincharadze");
    }

    @Test
    void activateDeactivate_shouldDeactivateWhenFalse() {
        ActivationDto dto = new ActivationDto(false);
        traineeController.activateDeactivateTrainee("dato.jincharadze", dto);
        verify(userService).deactivateUser("dato.jincharadze");
    }

    @Test
    void activateDeactivate_shouldActivateWhenTrue() {
        ActivationDto dto = new ActivationDto(true);
        traineeController.activateDeactivateTrainee("dato.jincharadze", dto);
        verify(userService).activateUser("dato.jincharadze");
    }
}