package com.gym.crm.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.Loader.*;
import com.gym.crm.Repository.*;
import com.gym.crm.dto.trainee.TraineeCreateDto;
import com.gym.crm.dto.trainer.TrainerCreateDto;
import com.gym.crm.dto.training.TrainingCreateDto;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageInitializer {
    private final TrainingTypeRepository trainingTypeRepository;
    private final List<Loader> loaders;

    @Value("${seed.file.path}")
    private Resource seedFile;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Getter
    private SeedData seedData;

    @PostConstruct
    public void init() {
        boolean alreadySeeded = !trainingTypeRepository.findAll().isEmpty();
        if (alreadySeeded) {
            log.info("Database already seeded. skipping");
            return;
        }

        try (InputStream is = seedFile.getInputStream()) {
            seedData = mapper.readValue(is, SeedData.class);
            loaders.forEach(l -> log.info("Loader in list: {} @ {}", l.getClass().getSimpleName(), System.identityHashCode(l)));

            loaders.stream()
                    .sorted(Comparator.comparingInt(Loader::getOrder))
                    .forEach(loader -> {
                        log.info("Running {}", loader.getClass().getSimpleName());
                        try {
                            loader.load(seedData);
                            log.info("Finished {}", loader.getClass().getSimpleName());
                        } catch (Exception e) {
                            log.error("FAILED in {}: {}", loader.getClass().getSimpleName(), e.getMessage(), e);
                            throw new IllegalStateException("Failed to load: " + loader.getClass().getSimpleName(), e);
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
        private List<TraineeCreateDto> trainees;
        private List<TrainerCreateDto> trainers;
        private List<TrainingCreateDto> trainings;
    }
}