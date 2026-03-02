package com.gym.crm.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.Loader.*;
import com.gym.crm.Repository.*;
import com.gym.crm.dto.TraineeDto;
import com.gym.crm.dto.TrainerDto;
import com.gym.crm.dto.TrainingDto;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageInitializer {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserRepository userRepository;
    private final List<Loader> loaders;

    @Value("${seed.file.path}")
    private Resource seedFile;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Getter
    private SeedData seedData;

    @PostConstruct
    public void init() {

        if (trainingTypeRepository.count() > 15) {
            log.info("Database already seeded — skipping");
            return;
        }


        try (InputStream is = seedFile.getInputStream()) {
            seedData = mapper.readValue(is, SeedData.class);
            loaders.forEach(l -> log.info("Loader in list: {} @ {}", l.getClass().getSimpleName(), System.identityHashCode(l)));
            for (Loader loader : loaders) {
                if (loader instanceof TrainingTypeLoader l) {
                    l.setTrainingTypes(seedData.getTrainingTypes());
                } else if (loader instanceof TraineeLoader l) {
                    l.setTrainees(seedData.getTrainees());
                } else if (loader instanceof TrainerLoader l) {
                    l.setTrainers(seedData.getTrainers());
                } else if (loader instanceof TrainingLoader l) {
                    l.setTrainings(seedData.getTrainings());
                }
            }

            loaders.stream()
                    .sorted(Comparator.comparingInt(Loader::getOrder))
                    .forEach(loader -> {
                        log.info("Running {}", loader.getClass().getSimpleName());
                        try {
                            loader.load();
                            log.info("Finished {}", loader.getClass().getSimpleName());
                        } catch (Exception e) {
                            log.error("FAILED in {}: {}", loader.getClass().getSimpleName(), e.getMessage(), e);
                        }
                    });

            log.info("Database seeding completed");

        } catch (Exception e) {
            log.error("Database seeding failed", e);
            throw new IllegalStateException("Seeding failed", e);
        }
    }


    @Getter
    public static class SeedData {
        private List<String> trainingTypes;
        private List<TraineeDto> trainees;
        private List<TrainerDto> trainers;
        private List<TrainingDto> trainings;
    }
}