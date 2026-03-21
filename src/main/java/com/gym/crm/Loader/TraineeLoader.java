package com.gym.crm.Loader;

import com.gym.crm.dto.TraineeDto;
import com.gym.crm.service.TraineeService;
import com.gym.crm.storage.StorageInitializer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;


import java.util.List;


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
        for (TraineeDto dto : seedData.getTrainees()) {
            traineeService.createTrainee(dto);
        }
    }
}