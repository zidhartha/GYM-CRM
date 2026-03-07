package com.gym.crm.Loader;

import com.gym.crm.Repository.TrainingTypeRepository;
import com.gym.crm.model.TrainingType;
import com.gym.crm.storage.StorageInitializer;
import com.gym.crm.storage.StorageInitializer.SeedData;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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