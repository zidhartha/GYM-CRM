package com.gym.crm.storage;

import com.gym.crm.model.TrainerWorkload;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WorkloadStorage {
    private final Map<String, TrainerWorkload> storage = new ConcurrentHashMap<>();

    public Map<String,TrainerWorkload> getStorage(){
        return this.storage;
    }

}
