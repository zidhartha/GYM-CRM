package com.gym.crm.storage;

import com.gym.crm.Util.IdGenerator;
import com.gym.crm.model.TrainingType;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Getter
public class TrainingTypeStorage {

    private final Map<Long, TrainingType> storage = new HashMap<>();
    private IdGenerator idGenerator;

    @Autowired
    public void setIdGenerator(IdGenerator idGenerator){
        this.idGenerator = idGenerator;
    }

    public TrainingType addTrainingType(String name) {
        TrainingType type = new TrainingType(idGenerator.generateNextId(), name);
        storage.put(type.getId(), type);
        return type;
    }

    public TrainingType get(Long id) {
        return storage.get(id);
    }


    public List<TrainingType> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<TrainingType> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }



    @PostConstruct
    public void init() {
        addTrainingType("Leg day");
        addTrainingType("Push day");
        addTrainingType("Pull day");
    }


    public Map<String, TrainingType> getAllByName() {
        Map<String, TrainingType> typesByName = new HashMap<>();
        for (TrainingType type : storage.values()) {
            typesByName.put(type.getName(), type);
        }
        return Collections.unmodifiableMap(typesByName);
    }
}
