package com.gym.crm.Loader;

import com.gym.crm.Repository.TrainingTypeRepository;
import com.gym.crm.model.TrainingType;
import com.gym.crm.storage.StorageInitializer.SeedData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TrainingTypeLoader implements Loader {

    private final TrainingTypeRepository trainingTypeRepository;
    private List<String> trainingTypes;

    public void setTrainingTypes(List<String> trainingTypes) {
        this.trainingTypes = trainingTypes;
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public void load() {

        if (trainingTypes == null) return;

        for (String typeName : trainingTypes) {
            trainingTypeRepository.save(new TrainingType(typeName));
        }
    }
}