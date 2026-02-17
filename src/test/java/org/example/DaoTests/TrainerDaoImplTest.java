package org.example.DaoTests;


import com.gym.crm.Exceptions.TraineeNotFoundException;
import com.gym.crm.Exceptions.TrainerNotFoundException;
import com.gym.crm.dao.impl.TrainerDaoImpl;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.storage.TrainerStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainerDaoImplTest {

    private TrainerDaoImpl trainerDao;
    private TrainerStorage storage;

    @BeforeEach
    void setUp() {
        storage = new TrainerStorage();
        trainerDao = new TrainerDaoImpl(storage);
    }

    @Test
    void saveAndFindById() {
        Trainer t = new Trainer();
        t.setId(1L);
        t.setFirstName("Jane");
        t.setLastName("Smith");
        t.setSpecialization(new TrainingType(1L, "Strength"));

        trainerDao.save(t);

        Optional<Trainer> found = trainerDao.findById(1L);
        assertTrue(found.isPresent());
        assertEquals("Jane", found.get().getFirstName());
    }

    @Test
    void updateTrainer() {
        Trainer t = new Trainer();
        t.setId(1L);
        t.setFirstName("Jane");
        trainerDao.save(t);

        t.setFirstName("Janet");
        trainerDao.update(t);

        assertEquals("Janet", trainerDao.findById(1L).get().getFirstName());
    }

    @Test
    void deleteTrainer() {
        Trainer t = new Trainer();
        t.setId(1L);
        trainerDao.save(t);

        trainerDao.delete(1L);

        assertThrows(TrainerNotFoundException.class, () -> trainerDao.findById(t.getId()));
    }

    @Test
    void findAllReturnsAll() {
        Trainer t1 = new Trainer();
        t1.setId(1L);
        Trainer t2 = new Trainer();
        t2.setId(2L);

        trainerDao.save(t1);
        trainerDao.save(t2);

        List<Trainer> all = trainerDao.findAll();
        assertEquals(2, all.size());
    }
}
