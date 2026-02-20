package com.gym.crm.Loader;
import com.gym.crm.model.TrainingType;
import com.gym.crm.storage.TrainingTypeStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TrainingTypeLoader {

    private static final Logger log = LoggerFactory.getLogger(TrainingTypeLoader.class);

    private final TrainingTypeStorage trainingTypeStorage;

    public TrainingTypeLoader(TrainingTypeStorage trainingTypeStorage) {
        this.trainingTypeStorage = trainingTypeStorage;
    }

    public Map<String, TrainingType> load(List<String> trainingTypes) {
        Map<String, TrainingType> map = new HashMap<>();

        if (trainingTypes == null || trainingTypes.isEmpty()) {
            log.warn("No training types found for seeding");
            return map;
        }

        long id = 1;

        for (String name : trainingTypes) {
            validate(name);

            TrainingType type = new TrainingType(id++, name);
            trainingTypeStorage.getStorage().put(type.getId(), type);

            map.put(name, type);
            log.info("Seeded TrainingType: {} (ID={})", name, type.getId());
        }


        return map;
    }

    private void validate(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Training type name cannot be null or empty");
        }
    }
}
