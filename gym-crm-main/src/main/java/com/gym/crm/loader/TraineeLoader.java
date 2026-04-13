package com.gym.crm.loader;

import com.gym.crm.dto.trainee.TraineeCreateDto;
import com.gym.crm.service.TraineeService;
import com.gym.crm.storage.StorageInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TraineeLoader implements Loader {
    private final TraineeService traineeService;

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public void load(StorageInitializer.SeedData seedData) {
        if (seedData.getTrainees() == null) return;
        for (TraineeCreateDto dto : seedData.getTrainees()) {
            traineeService.createTrainee(dto);
        }
    }
}