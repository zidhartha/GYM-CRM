package org.example.DaoTests;

import com.gym.crm.Exceptions.TraineeNotFoundException;
import com.gym.crm.dao.impl.TraineeDaoImpl;
import com.gym.crm.model.Trainee;
import com.gym.crm.storage.TraineeStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TraineeDaoImplTest {

    private TraineeDaoImpl traineeDao;
    private TraineeStorage storage;
    @BeforeEach
    void setUp() {
        storage = new TraineeStorage();
        traineeDao = new TraineeDaoImpl(storage);
    }

    @Test
    void saveAndFindById() {
        Trainee t = new Trainee();
        t.setId(1L);
        t.setFirstName("John");
        t.setLastName("Doe");
        t.setDateOfBirth(LocalDate.of(2000, 1, 1));
        t.setAddress("Tbilisi");

        traineeDao.save(t);

        Optional<Trainee> found = traineeDao.findById(1L);
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
    }

    @Test
    void updateTrainee() {
        Trainee t = new Trainee();
        t.setId(1L);
        t.setFirstName("John");
        t.setLastName("Doe");
        t.setDateOfBirth(LocalDate.of(2000, 1, 1));
        t.setAddress("Tbilisi");

        traineeDao.save(t);

        t.setAddress("Batumi");
        traineeDao.update(t);

        assertEquals("Batumi", traineeDao.findById(1L).get().getAddress());
    }

    @Test
    void deleteTrainee() {
        Trainee t = new Trainee();
        t.setFirstName("John");
        t.setLastName("Doe");
        traineeDao.save(t);

        traineeDao.delete(t.getId());

        assertThrows(TraineeNotFoundException.class, () -> traineeDao.findById(t.getId()));
    }


    @Test
    void findAllReturnsAll() {
        Trainee t1 = new Trainee();
        t1.setId(1L);
        Trainee t2 = new Trainee();
        t2.setId(2L);

        traineeDao.save(t1);
        traineeDao.save(t2);

        List<Trainee> all = traineeDao.findAll();
        assertEquals(2, all.size());
    }
}

