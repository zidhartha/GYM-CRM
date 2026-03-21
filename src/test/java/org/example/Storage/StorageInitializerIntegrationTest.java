package org.example.Storage;

import com.gym.crm.Repository.TraineeRepository;
import com.gym.crm.Repository.TrainerRepository;
import com.gym.crm.Repository.TrainingRepository;
import com.gym.crm.config.AppConfig;
import com.gym.crm.model.Trainee;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StorageInitializerIntegrationTest {

    @Test
    void storageIsPopulatedOnStartup() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext()) {

            context.getEnvironment().setActiveProfiles("seed");
            context.register(AppConfig.class);
            context.refresh();

            TraineeRepository traineeRepository = context.getBean(TraineeRepository.class);
            TrainerRepository trainerRepository = context.getBean(TrainerRepository.class);
            TrainingRepository trainingRepository = context.getBean(TrainingRepository.class);

            assertTrue(!traineeRepository.findAll().isEmpty(), "Trainees should be loaded");
            assertTrue(!trainerRepository.findAll().isEmpty(), "Trainers should be loaded");
            assertTrue(!trainingRepository.findAll().isEmpty(), "Trainings should be loaded");
        }
    }

    @Test
    void loadedTraineesHaveValidUsernames() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext()) {

            context.getEnvironment().setActiveProfiles("seed");
            context.register(AppConfig.class);
            context.refresh();

            TraineeRepository traineeRepository = context.getBean(TraineeRepository.class);
            List<Trainee> trainees = traineeRepository.findAll();

            assertFalse(trainees.isEmpty(), "Trainees should be loaded");

            trainees.forEach(t -> {
                assertNotNull(t.getUser().getUsername(), "Username should not be null");
                assertFalse(t.getUser().getUsername().isEmpty(), "Username should not be empty");
                assertTrue(t.getUser().getUsername().contains("."), "Username should contain a dot");
            });
        }
    }
}