package com.gym.crm.storage;

import com.gym.crm.Repository.TrainingTypeRepository;
import com.gym.crm.model.TrainingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;
@Component
public class TrainingTypeStorage {
    private final TrainingTypeRepository repository;
    @Autowired
    public TrainingTypeStorage(TrainingTypeRepository repository){
        this.repository = repository;
    }

    public List<TrainingType> findAll() {
        return repository.findAll();
    }

    public Optional<TrainingType> findById(Long id) {
        return repository.findById(id);
    }
}
