package com.gym.crm.Loader;

import com.gym.crm.model.TrainingType;
import com.gym.crm.service.TrainingService;
import com.gym.crm.storage.StorageInitializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TrainingLoader {

    private final TrainingService trainingService;

    public TrainingLoader(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    public void load(List<StorageInitializer.TrainingSeed> trainings,
                     Map<String, TrainingType> typeMap) {

        if (trainings == null) return;

        for (var t : trainings) {
            TrainingType type = typeMap.get(t.getTrainingTypeName());

            if (type == null) {
                throw new IllegalStateException("Unknown training type: "
                        + t.getTrainingTypeName());
            }

            trainingService.createTraining(
                    t.getTraineeId(),
                    t.getTrainerId(),
                    t.getTrainingName(),
                    type,
                    t.getTrainingDate(),
                    t.getTrainingDurationMinutes()
            );
        }
    }
}
