package org.example.ControllerTests;

import com.gym.crm.controller.TrainerController;
import com.gym.crm.dto.trainee.TraineeListDto;
import com.gym.crm.dto.trainer.TrainerCreateDto;
import com.gym.crm.dto.trainer.TrainerProfileDto;
import com.gym.crm.dto.trainer.TrainerUpdateDto;
import com.gym.crm.exceptions.AccessDeniedException;
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
class TrainerControllerTest {

    @Mock TrainerService trainerService;
    @Mock TrainingService trainingService;
    @Mock UserService userService;
    @InjectMocks TrainerController trainerController;

    @Test
    void createTrainer_shouldReturn201() {
        TrainerCreateDto dto = new TrainerCreateDto("Gio", "Janelidze", "Yoga");
        when(trainerService.createTrainer(dto)).thenReturn(Map.of("username", "gio.janelidze", "password", "pass"));

        ResponseEntity<Map<String, String>> response = trainerController.createTrainer(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("gio.janelidze", response.getBody().get("username"));
    }

    @Test
    void getTrainer_shouldReturn200() {
        TrainerProfileDto profile = new TrainerProfileDto("Gio", "Janelidze", "gio.janelidze", "Yoga", true, new TraineeListDto(List.of()));
        when(trainerService.getTrainerByUsername("gio.janelidze")).thenReturn(profile);

        ResponseEntity<TrainerProfileDto> response = trainerController.getTrainer("gio.janelidze", "pass", "gio.janelidze");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Gio", response.getBody().getFirstName());
    }

    @Test
    void getTrainer_shouldThrowWhenAuthFails() {
        doThrow(new AccessDeniedException("Invalid credentials"))
                .when(userService).authenticate("gio.janelidze", "wrongpass");

        assertThrows(AccessDeniedException.class,
                () -> trainerController.getTrainer("gio.janelidze", "wrongpass", "gio.janelidze"));
    }

    @Test
    void updateTrainer_shouldReturn200() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setFirstName("Gio");
        dto.setLastName("Janelidze");
        TrainerProfileDto profile = new TrainerProfileDto("Gio", "Janelidze", "gio.janelidze", "Yoga", true, new TraineeListDto(List.of()));
        when(trainerService.updateTrainer(dto, "gio.janelidze")).thenReturn(profile);

        ResponseEntity<TrainerProfileDto> response = trainerController.updateTrainer("gio.janelidze", "pass", "gio.janelidze", dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Gio", response.getBody().getFirstName());
    }

    @Test
    void activateDeactivate_shouldActivateWhenTrue() {
        trainerController.activateDeactivateTrainer("gio.janelidze", "pass", "gio.janelidze", true);
        verify(userService).activateUser("gio.janelidze");
    }

    @Test
    void activateDeactivate_shouldDeactivateWhenFalse() {
        trainerController.activateDeactivateTrainer("gio.janelidze", "pass", "gio.janelidze", false);
        verify(userService).deactivateUser("gio.janelidze");
    }
}