package com.gym.crm.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.Loader.TraineeLoader;
import com.gym.crm.Loader.TrainerLoader;
import com.gym.crm.Loader.TrainingLoader;
import com.gym.crm.Loader.TrainingTypeLoader;
import com.gym.crm.model.TrainingType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class StorageInitializer {

    private final TrainingTypeLoader trainingTypeLoader;
    private final TraineeLoader traineeLoader;
    private final TrainerLoader trainerLoader;
    private final TrainingLoader trainingLoader;

    @Value("${data.storage}")
    private Resource dataFile;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public StorageInitializer(
            TrainingTypeLoader trainingTypeLoader,
            TraineeLoader traineeLoader,
            TrainerLoader trainerLoader,
            TrainingLoader trainingLoader
    ) {
        this.trainingTypeLoader = trainingTypeLoader;
        this.traineeLoader = traineeLoader;
        this.trainerLoader = trainerLoader;
        this.trainingLoader = trainingLoader;
    }

    @PostConstruct
    public void init() {
        try (InputStream is = dataFile.getInputStream()) {
            SeedData data = mapper.readValue(is, SeedData.class);

            Map<String, TrainingType> types =
                    trainingTypeLoader.load(data.getTrainingTypes());

            traineeLoader.load(data.getTrainees());
            trainerLoader.load(data.getTrainers(), types);
            trainingLoader.load(data.getTrainings(), types);

        } catch (Exception e) {
            throw new IllegalStateException("Storage initialization failed", e);
        }
    }

    @Getter
    @Setter
    public static class SeedData {

        private List<String> trainingTypes;
        private List<TraineeSeed> trainees;
        private List<TrainerSeed> trainers;
        private List<TrainingSeed> trainings;
    }

    @Getter
    @Setter
    public static class TraineeSeed{
        private String firstName;
        private String lastName;
        private String address;
        private LocalDate dateOfBirth;
    }

    @Getter
    @Setter
    public static class TrainerSeed{
        private String firstName;
        private String lastName;
        private String specializationName;
    }

    @Getter
    @Setter
    public static class TrainingSeed{
        private Long traineeId;
        private Long trainerId;
        private String trainingName;
        private String trainingTypeName;
        private LocalDate trainingDate;
        private Integer trainingDurationMinutes;
    }

}

