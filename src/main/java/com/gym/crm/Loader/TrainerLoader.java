package com.gym.crm.Loader;

import com.gym.crm.dto.TrainerDto;
import com.gym.crm.service.TrainerService;
import com.gym.crm.storage.StorageInitializer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
@RequiredArgsConstructor
public class TrainerLoader implements Loader {
    private final TrainerService trainerService;


    @Override
    public int getOrder() {
        return 4;
    }

    @Override
    public void load(StorageInitializer.SeedData seedData) {
        if (seedData.getTrainers() == null) return;
        for (TrainerDto dto : seedData.getTrainers()) {
            trainerService.createTrainer(dto);
        }
    }
}