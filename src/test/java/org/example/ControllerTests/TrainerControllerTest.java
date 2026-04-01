package org.example.ControllerTests;

import com.gym.crm.controller.TrainerController;
import com.gym.crm.dto.authentication.ActivationDto;
import com.gym.crm.dto.authentication.RegistrationResponseDto;
import com.gym.crm.dto.trainee.TraineeListDto;
import com.gym.crm.dto.trainer.TrainerCreateDto;
import com.gym.crm.dto.trainer.TrainerProfileDto;
import com.gym.crm.dto.trainer.TrainerUpdateDto;
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
        RegistrationResponseDto expected = RegistrationResponseDto.builder()
                .username("gio.janelidze")
                .password("pass")
                .build();
        when(trainerService.createTrainer(dto)).thenReturn(expected);

        ResponseEntity<RegistrationResponseDto> response = trainerController.createTrainer(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("gio.janelidze", response.getBody().getUsername());
    }

    @Test
    void getTrainer_shouldReturn200() {
        TrainerProfileDto profile = TrainerProfileDto.builder()
                .firstName("Gio")
                .lastName("Janelidze")
                .username("gio.janelidze")
                .specialization("Yoga")
                .isActive(true)
                .trainees(new TraineeListDto(List.of()))
                .build();
        when(trainerService.getTrainerByUsername("gio.janelidze")).thenReturn(profile);

        ResponseEntity<TrainerProfileDto> response = trainerController.getTrainer("gio.janelidze");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Gio", response.getBody().getFirstName());
    }

    @Test
    void updateTrainer_shouldReturn200() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setFirstName("Gio");
        dto.setLastName("Janelidze");
        dto.setIsActive(true);

        TrainerProfileDto profile = TrainerProfileDto.builder()
                .firstName("Gio")
                .lastName("Janelidze")
                .username("gio.janelidze")
                .specialization("Yoga")
                .isActive(true)
                .trainees(new TraineeListDto(List.of()))
                .build();
        when(trainerService.updateTrainer(dto, "gio.janelidze")).thenReturn(profile);

        ResponseEntity<TrainerProfileDto> response = trainerController.updateTrainer("gio.janelidze", dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Gio", response.getBody().getFirstName());
    }

    @Test
    void activateDeactivate_shouldActivateWhenTrue() {
        ActivationDto dto = new ActivationDto(true);
        trainerController.activateDeactivateTrainer("gio.janelidze", dto);
        verify(userService).activateUser("gio.janelidze");
    }

    @Test
    void activateDeactivate_shouldDeactivateWhenFalse() {
        ActivationDto dto = new ActivationDto(false);
        trainerController.activateDeactivateTrainer("gio.janelidze", dto);
        verify(userService).deactivateUser("gio.janelidze");
    }
}