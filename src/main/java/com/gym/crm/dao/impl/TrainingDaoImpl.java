package com.gym.crm.dao.impl;

import com.gym.crm.Exceptions.TrainingNotFoundException;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.model.Training;
import com.gym.crm.storage.TrainingStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TrainingDaoImpl implements TrainingDao {
    private final TrainingStorage trainingStorage;
    private static final Logger log = LoggerFactory.getLogger(TrainingDaoImpl.class);

    private long idCounter = 0;

    public TrainingDaoImpl(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public Training save(Training training) {
        log.info("Saving a training");
        if (training == null) {
            log.error("The training passed in is null.");
            throw new TrainingNotFoundException();
        }

        if (training.getId() == null) {
            training.setId(++idCounter);
            log.debug("Generated ID: {}", training.getId());
        }

        trainingStorage.getStorage().put(training.getId(), training);
        log.info("Saved the training successfully with ID: {}", training.getId());
        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        log.info("Attempting to find a training with the id {}", id);

        Training training = trainingStorage.getStorage().get(id);

        if (training == null) {
            log.warn("Training with the id {} does not exist", id);
            throw new TrainingNotFoundException();
        }

        return Optional.of(training);
    }

    @Override
    public List<Training> findAll() {
        log.info("Attempting to collect every training");
        ArrayList<Training> all = new ArrayList<>(trainingStorage.getStorage().values());
        log.info("Successfully found {} trainings", all.size());
        return all;
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting the training with the id {}", id);
        if (!trainingStorage.getStorage().containsKey(id)) {
            log.warn("Training with the id {} does not exist", id);
            return;
        }
        trainingStorage.getStorage().remove(id);
        log.info("Successfully deleted the training with the id {}", id);
    }

    @Override
    public List<Training> findByTrainerId(Long trainerId) {
        log.info("Finding trainings for trainer with id {}", trainerId);

        List<Training> trainings = trainingStorage.getStorage().values().stream()
                .filter(training -> training.getTrainerId().equals(trainerId))
                .collect(Collectors.toList());

        log.info("Found {} trainings for trainer with id {}", trainings.size(), trainerId);
        return trainings;
    }

    @Override
    public List<Training> findByTraineeId(Long traineeId) {
        log.info("Finding trainings for trainee with id {}", traineeId);

        List<Training> trainings = trainingStorage.getStorage().values().stream()
                .filter(training -> training.getTraineeId().equals(traineeId))
                .collect(Collectors.toList());

        log.info("Found {} trainings for trainee with id {}", trainings.size(), traineeId);
        return trainings;
    }
}