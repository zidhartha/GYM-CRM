package com.gym.crm.Loader;


import com.gym.crm.dto.TrainingDto;

import com.gym.crm.service.TrainingService;


import com.gym.crm.storage.StorageInitializer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;


import java.util.List;

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
        for (TrainingDto dto : seedData.getTrainings()) {
            trainingService.createTraining(dto);
        }
    }
}