package com.gym.crm.storage;

import com.gym.crm.model.TrainingType;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Getter
public class TrainingTypeStorage {

    private final Map<Long, TrainingType> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    private long nextId = 1;

    public TrainingType addTrainingType(String name) {
        TrainingType type = new TrainingType(nextId++, name);
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

    public Optional<TrainingType> findByName(String name) {
        return storage.values().stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst();
    }


    @PostConstruct
    public void init() {
        addTrainingType("Cardio");
        addTrainingType("Strength");
        addTrainingType("Yoga");
    }


}
