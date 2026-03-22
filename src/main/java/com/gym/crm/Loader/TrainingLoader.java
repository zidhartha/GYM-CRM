package com.gym.crm.Loader;


import com.gym.crm.dto.training.TrainingCreateDto;

import com.gym.crm.service.TrainingService;


import com.gym.crm.storage.StorageInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingLoader implements Loader {
    private final TrainingService trainingService;


    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    public void load(StorageInitializer.SeedData seedData) {
        if (seedData.getTrainings() == null) return;
        for (TrainingCreateDto dto : seedData.getTrainings()) {
            trainingService.createTraining(dto);
        }
    }
}