package com.gym.crm.storage;

import com.gym.crm.model.TrainingType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrainingTypeStorage {
    private Map<Long, TrainingType> trainingTypeMap = new HashMap<>();

    public Map<Long, TrainingType> getStorage(){
        return trainingTypeMap;
    }

}
