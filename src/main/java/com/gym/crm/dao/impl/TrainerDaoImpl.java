package com.gym.crm.dao.impl;

import com.gym.crm.Exceptions.TrainerNotFoundException;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainer;
import com.gym.crm.storage.TrainerStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDaoImpl implements TrainerDao {
    private final TrainerStorage trainerStorage;
    private static final Logger log = LoggerFactory.getLogger(TrainerDaoImpl.class);

    private long idCounter = 0;

    public TrainerDaoImpl(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public Trainer save(Trainer trainer) {
        log.info("Saving a trainer...");
        if (trainer == null) {
            log.error("The trainer passed in is null.");
            throw new TrainerNotFoundException();
        }


        if (trainer.getId() == null) {
            trainer.setId(++idCounter);
            log.debug("Generated ID: {}", trainer.getId());
        }

        trainerStorage.getStorage().put(trainer.getId(), trainer);
        log.info("Saved the trainer successfully with ID: {}", trainer.getId());
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        Long id = trainer.getId();
        log.info("Updating the trainer with the id {}", id);
        if (trainerStorage.getStorage().containsKey(id)) {
            trainerStorage.getStorage().put(id, trainer);
            log.info("Successfully updated the trainer with id {}", id);
            return trainer;
        }
        log.warn("The trainer with the id {} does not exist in the database", id);
        throw new TrainerNotFoundException();
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        log.info("Attempting to find a trainer with the id {}", id);

        Trainer trainer = trainerStorage.getStorage().get(id);

        if (trainer == null) {
            log.warn("Trainer with the id {} does not exist", id);
            throw new TrainerNotFoundException();
        }

        return Optional.of(trainer);
    }

    @Override
    public List<Trainer> findAll() {
        log.info("Attempting to collect every trainer");
        ArrayList<Trainer> all = new ArrayList<>(trainerStorage.getStorage().values());
        log.info("Successfully found {} trainers", all.size());
        return all;
    }

    @Override
    public void delete(long id) {
        log.info("Deleting the trainer with the id {}", id);
        if (!trainerStorage.getStorage().containsKey(id)) {
            log.warn("Trainer with the id {} does not exist in the storage", id);
            return;
        }
        trainerStorage.getStorage().remove(id);
        log.info("Deleted the trainer with the id {} successfully", id);
    }
}