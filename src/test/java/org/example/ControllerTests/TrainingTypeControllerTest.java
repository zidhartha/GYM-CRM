package org.example.ControllerTests;


import com.gym.crm.controller.TrainingTypeController;
import com.gym.crm.dto.trainingType.TrainingTypeListDto;
import com.gym.crm.dto.trainingType.TrainingTypeProfileDto;
import com.gym.crm.service.TrainingTypeService;
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
class TrainingTypeControllerTest {

    @Mock TrainingTypeService trainingTypeService;
    @InjectMocks TrainingTypeController trainingTypeController;

    @Test
    void getAllTrainingTypes_shouldReturn200() {
        TrainingTypeListDto expected = new TrainingTypeListDto(List.of(
                new TrainingTypeProfileDto(1L, "Yoga"),
                new TrainingTypeProfileDto(2L, "Cardio")
        ));
        when(trainingTypeService.getAllTrainingTypes()).thenReturn(expected);

        ResponseEntity<TrainingTypeListDto> response = trainingTypeController.getAllTrainingTypes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getTrainingTypes().size());
        assertEquals("Yoga", response.getBody().getTrainingTypes().get(0).getTrainingType());
        verify(trainingTypeService).getAllTrainingTypes();
    }

    @Test
    void getAllTrainingTypes_shouldReturnEmptyList() {
        when(trainingTypeService.getAllTrainingTypes()).thenReturn(new TrainingTypeListDto(List.of()));

        ResponseEntity<TrainingTypeListDto> response = trainingTypeController.getAllTrainingTypes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getTrainingTypes().isEmpty());
    }
}