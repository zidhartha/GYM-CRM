package com.gym.crm.loader;

import com.gym.crm.repository.TrainingTypeRepository;
import com.gym.crm.model.TrainingType;
import com.gym.crm.storage.StorageInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingTypeLoader implements Loader {
    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public void load(StorageInitializer.SeedData seedData) {
        if (seedData.getTrainingTypes() == null) return;
        for (String typeName : seedData.getTrainingTypes()) {
            trainingTypeRepository.save(new TrainingType(typeName));
        }
    }
}