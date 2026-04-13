package com.gym.crm.UtilTests;


import com.gym.crm.util.EntityMapper;
import com.gym.crm.dto.trainee.TraineeListDto;
import com.gym.crm.dto.trainer.TrainerListDto;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityMapperTest {

    private final EntityMapper entityMapper = new EntityMapper();

    @Test
    void mapToTrainerListDto_shouldMapCorrectly() {
        User user = new User("Gio", "Jincharadze", "gio.jincharadze", "pass");
        TrainingType type = new TrainingType();
        type.setId(1L);
        Trainer trainer = new Trainer(user, type);

        TrainerListDto result = entityMapper.mapToTrainerListDto(List.of(trainer));

        assertEquals(1, result.getTrainerList().size());
        assertEquals("gio.jincharadze", result.getTrainerList().get(0).getUsername());
        assertEquals("Gio", result.getTrainerList().get(0).getFirstName());
        assertEquals("Jincharadze", result.getTrainerList().get(0).getLastName());
        assertEquals(1L, result.getTrainerList().get(0).getSpecialization());
    }

    @Test
    void mapToTrainerListDto_shouldReturnEmptyList() {
        TrainerListDto result = entityMapper.mapToTrainerListDto(List.of());
        assertNotNull(result);
        assertTrue(result.getTrainerList().isEmpty());
    }

    @Test
    void mapToTraineeListDto_shouldMapCorrectly() {
        User user = new User("Gio", "Janelidze", "gio.janelidze", "pass");
        Trainee trainee = new Trainee(user, null, null);

        TraineeListDto result = entityMapper.mapToTraineeListDto(List.of(trainee));

        assertEquals(1, result.getTraineeList().size());
        assertEquals("gio.janelidze", result.getTraineeList().get(0).getUsername());
        assertEquals("Gio", result.getTraineeList().get(0).getFirstname());
        assertEquals("Janelidze", result.getTraineeList().get(0).getLastname());
    }

    @Test
    void mapToTraineeListDto_shouldReturnEmptyList() {
        TraineeListDto result = entityMapper.mapToTraineeListDto(List.of());
        assertNotNull(result);
        assertTrue(result.getTraineeList().isEmpty());
    }
}