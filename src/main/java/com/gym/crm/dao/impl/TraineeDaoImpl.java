package com.gym.crm.dao.impl;

import com.gym.crm.Exceptions.TraineeNotFoundException;
import com.gym.crm.dao.TraineeDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.storage.TraineeStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDaoImpl implements TraineeDao {
    private final TraineeStorage traineeStorage;
    private static final Logger log = LoggerFactory.getLogger(TraineeDaoImpl.class);

    private long idCounter = 0;

    public TraineeDaoImpl(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public Trainee save(Trainee trainee) {
        log.info("Saving a trainee.");
        if (trainee == null) {
            log.error("The trainee passed in is null.");
            throw new TraineeNotFoundException();
        }

        if (trainee.getId() == null) {
            trainee.setId(++idCounter);
            log.debug("Generated ID: {}", trainee.getId());
        }

        traineeStorage.getStorage().put(trainee.getId(), trainee);
        log.info("Saved the trainee successfully with ID: {}", trainee.getId());
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        Long id = trainee.getId();
        log.info("Updating the trainee with the id {}", id);
        if (traineeStorage.getStorage().containsKey(id)) {
            traineeStorage.getStorage().put(id, trainee);
            log.info("Successfully updated the trainee with the id {}", id);
            return trainee;
        }
        log.warn("The trainee with the id {} does not exist", id);
        throw new TraineeNotFoundException();
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        log.info("Attempting to find a trainee with the id {}", id);

        Trainee trainee = traineeStorage.getStorage().get(id);

        if (trainee == null) {
            log.warn("The trainee with id {} does not exist", id);
            throw new TraineeNotFoundException();
        }

        return Optional.of(trainee);
    }

    @Override
    public List<Trainee> findAll() {
        log.info("Attempting to collect every trainee");
        ArrayList<Trainee> all = new ArrayList<>(traineeStorage.getStorage().values());
        log.info("Successfully found {} trainees", all.size());
        return all;
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting the trainee with the id {}", id);
        if (!traineeStorage.getStorage().containsKey(id)) {
            log.warn("Trainee with the id {} does not exist", id);
            return;
        }
        traineeStorage.getStorage().remove(id);
        log.info("Successfully deleted the trainee with the id {}", id);
    }
}