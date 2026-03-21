package org.example.ControllerTests;

import com.gym.crm.controller.TraineeController;
import com.gym.crm.dto.trainee.TraineeCreateDto;
import com.gym.crm.dto.trainee.TraineeProfileDto;
import com.gym.crm.dto.trainee.TraineeUpdateDto;
import com.gym.crm.dto.trainer.TrainerListDto;
import com.gym.crm.exceptions.AccessDeniedException;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

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
        when(traineeService.createTrainee(dto)).thenReturn(Map.of("username", "dato.jincharadze", "password", "pass"));

        ResponseEntity<Map<String, String>> response = traineeController.createTrainee(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("dato.jincharadze", response.getBody().get("username"));
    }

    @Test
    void getTraineeProfile_shouldReturn200() {
        TraineeProfileDto profile = new TraineeProfileDto("Dato", "Jincharadze", null, null, true, new TrainerListDto(List.of()));
        when(traineeService.getTraineeProfile("dato.jincharadze")).thenReturn(profile);

        ResponseEntity<TraineeProfileDto> response = traineeController.getTraineeProfile("dato.jincharadze", "pass", "dato.jincharadze");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dato", response.getBody().getFirstName());
    }

    @Test
    void getTraineeProfile_shouldThrowWhenAuthFails() {
        doThrow(new AccessDeniedException("Invalid credentials"))
                .when(userService).authenticate("dato.jincharadze", "wrongpass");

        assertThrows(AccessDeniedException.class,
                () -> traineeController.getTraineeProfile("dato.jincharadze", "wrongpass", "dato.jincharadze"));
    }

    @Test
    void updateTraineeProfile_shouldReturn200() {
        TraineeUpdateDto dto = new TraineeUpdateDto();
        TraineeProfileDto profile = new TraineeProfileDto("Dato", "Janelidze", null, null, true, new TrainerListDto(List.of()));
        when(traineeService.updateTraineeProfile(dto, "dato.jincharadze")).thenReturn(profile);

        ResponseEntity<TraineeProfileDto> response = traineeController.updateTraineeProfile("dato.jincharadze", "pass", "dato.jincharadze", dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteTrainee_shouldReturn200() {
        ResponseEntity<Void> response = traineeController.deleteTraineeProfile("dato.jincharadze", "pass", "dato.jincharadze");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(traineeService).deleteTrainee("dato.jincharadze");
    }

    @Test
    void activateDeactivate_shouldDeactivateWhenFalse() {
        traineeController.activateDeactivateTrainee("dato.jincharadze", "pass", "dato.jincharadze", false);
        verify(userService).deactivateUser("dato.jincharadze");
    }

    @Test
    void activateDeactivate_shouldActivateWhenTrue() {
        traineeController.activateDeactivateTrainee("dato.jincharadze", "pass", "dato.jincharadze", true);
        verify(userService).activateUser("dato.jincharadze");
    }
}