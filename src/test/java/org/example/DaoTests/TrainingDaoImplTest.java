package org.example.DaoTests;


import com.gym.crm.Exceptions.TrainingNotFoundException;
import com.gym.crm.dao.impl.TrainingDaoImpl;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.storage.TrainingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainingDaoImplTest {

    private TrainingDaoImpl trainingDao;
    private TrainingStorage trainingStorage;

    @BeforeEach
    void setUp() {
        trainingStorage = new TrainingStorage();
        trainingDao = new TrainingDaoImpl(trainingStorage);
    }

    private Training createTraining() {
        Training t = new Training();
        t.setTraineeId(1L);
        t.setTrainerId(2L);
        t.setTrainingName("Leg Day");
        t.setTrainingType(new TrainingType("Strength"));
        t.setTrainingDate(LocalDate.now());
        t.setTrainingDuration(60);
        return t;
    }

    @Test
    void saveTraining_success() {
        Training training = createTraining();

        Training saved = trainingDao.save(training);

        assertNotNull(saved.getId());
        assertEquals("Leg Day", saved.getTrainingName());
    }

    @Test
    void saveTraining_null_throwsException() {
        assertThrows(TrainingNotFoundException.class,
                () -> trainingDao.save(null));
    }

    @Test
    void findById_success() {
        Training training = trainingDao.save(createTraining());

        Training found = trainingDao.findById(training.getId()).get();

        assertEquals(training.getId(), found.getId());
    }

    @Test
    void findById_notFound_throwsException() {
        assertThrows(TrainingNotFoundException.class,
                () -> trainingDao.findById(999L));
    }

    @Test
    void findAll_returnsAllTrainings() {
        trainingDao.save(createTraining());
        trainingDao.save(createTraining());

        assertEquals(2, trainingDao.findAll().size());
    }



    @Test
    void deleteTraining_success() {
        Training training = trainingDao.save(createTraining());

        trainingDao.delete(training.getId());

        assertThrows(TrainingNotFoundException.class,
                () -> trainingDao.findById(training.getId()));
    }

    @Test
    void deleteTraining_notFound_doesNothing() {
        assertDoesNotThrow(() -> trainingDao.delete(999L));
    }
}
