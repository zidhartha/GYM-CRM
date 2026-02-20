package com.gym.crm.Loader;

import com.gym.crm.model.TrainingType;
import com.gym.crm.service.TrainerService;
import com.gym.crm.storage.StorageInitializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TrainerLoader {

    private final TrainerService trainerService;

    public TrainerLoader(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    public void load(List<StorageInitializer.TrainerSeed> trainers,
                     Map<String, TrainingType> typeMap) {

        if (trainers == null) return;

        trainers.forEach( t -> {
            TrainingType specialization = typeMap.get(t.getSpecializationName());
            if(specialization == null){
                throw new IllegalStateException("Unknown training type: " + specialization);
            }

            trainerService.createTrainer(
                    t.getFirstName(),
                    t.getLastName(),
                    specialization
            );
        });

    }
}

