package com.gym.crm.ControllerTests;

import com.gym.crm.controller.TrainingController;
import com.gym.crm.dto.training.TrainingCreateDto;
import com.gym.crm.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    @Mock TrainingService trainingService;
    @InjectMocks TrainingController trainingController;

    @Test
    void createTraining_shouldReturn200() {
        TrainingCreateDto dto = TrainingCreateDto.builder()
                .traineeUsername("dato.jincharadze")
                .trainerUsername("gio.janelidze")
                .trainingName("Morning Yoga")
                .trainingTypeName("Yoga")
                .trainingDate(LocalDate.now())
                .trainingDuration(60L)
                .build();

        ResponseEntity<Void> response = trainingController.createTraining(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(trainingService).createTraining(dto);
    }

    @Test
    void createTraining_shouldThrowWhenTraineeNotFound() {
        TrainingCreateDto dto = TrainingCreateDto.builder()
                .traineeUsername("unknown")
                .trainerUsername("gio.janelidze")
                .trainingName("Morning Yoga")
                .trainingTypeName("Yoga")
                .trainingDate(LocalDate.now())
                .trainingDuration(60L)
                .build();

        doThrow(new IllegalArgumentException("Trainee not found"))
                .when(trainingService).createTraining(dto);

        assertThrows(IllegalArgumentException.class,
                () -> trainingController.createTraining(dto));
    }
}