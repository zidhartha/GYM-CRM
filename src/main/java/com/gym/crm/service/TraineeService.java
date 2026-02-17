package com.gym.crm.service;

import com.gym.crm.Exceptions.TraineeNotFoundException;
import com.gym.crm.Util.IdGenerator;
import com.gym.crm.Util.PasswordGenerator;
import com.gym.crm.Util.UsernameGenerator;
import com.gym.crm.dao.impl.TraineeDaoImpl;
import com.gym.crm.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    private PasswordGenerator passwordGenerator;
    private UsernameGenerator usernameGenerator;
    private TraineeDaoImpl traineeDao;
    private IdGenerator idGenerator;

    @Autowired
    public void setIdGenerator(IdGenerator idGenerator){
        this.idGenerator = idGenerator;
        log.debug("IdGenerator injected into TraineeService");
    }

    @PostConstruct
    public void initIdGenerator() {
        idGenerator.initialize(
                traineeDao.findAll().stream()
                        .collect(Collectors.toMap(Trainee::getId, t -> t))
        );
        log.debug("IdGenerator initialized with existing trainee IDs");
    }

    @Autowired
    public void setTraineeDao(TraineeDaoImpl traineeDao){
        this.traineeDao = traineeDao;
        log.debug("TraineeDaoImpl injected into TraineeService");
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
        log.debug("UsernameGenerator injected into TraineeService");
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
        log.debug("PasswordGenerator injected into TraineeService");
    }


    public Trainee createTrainee(String firstName, String lastName,
                                 LocalDate dateOfBirth, String address) {
        log.info("Creating a new Trainee: {} {}", firstName, lastName);

        validateTraineeInput(firstName, lastName, dateOfBirth, address);

        String username = usernameGenerator.generateUsername(firstName, lastName);
        log.info("Generated username: {}", username);


        String password = passwordGenerator.generatePassword();
        log.debug("Generated password");

        Trainee trainee = new Trainee();
        trainee.setId(idGenerator.generateNextId());
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setActive(true);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        Trainee saved = traineeDao.save(trainee);
        log.info("Trainee created successfully: {}", username);

        return saved;
    }


    public Trainee updateTrainee(Long id, String firstName, String lastName,
                                 LocalDate dateOfBirth, String address, Boolean isActive) {
        log.info("Updating trainee id={}", id);

        if (id == null) {
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }
        validateTraineeInput(firstName, lastName, dateOfBirth, address);

        Trainee trainee = traineeDao.findById(id)
                .orElseThrow(() -> {
                    log.error("Trainee not found with id: {}", id);
                    return new TraineeNotFoundException();
                });

        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        if (isActive != null) {
            trainee.setActive(isActive);
        }

        Trainee updated = traineeDao.update(trainee);
        log.info("Trainee updated successfully: {}", id);

        return updated;
    }

    public void deleteTrainee(Long id) {
        log.info("Deleting trainee id={}", id);

        if (id == null) {
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }

        traineeDao.findById(id)
                .orElseThrow(() -> {
                    log.error("Trainee not found with id: {}", id);
                    return new TraineeNotFoundException();
                });

        traineeDao.delete(id);
        log.info("Trainee deleted successfully: {}", id);
    }

    public Trainee select(Long id) {
        log.info("Selecting trainee with id: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }

        return traineeDao.findById(id)
                .orElseThrow(() -> {
                    log.error("Trainee not found with id: {}", id);
                    return new TraineeNotFoundException();
                });
    }


    public List<Trainee> selectAllTrainees() {
        log.info("Selecting all trainees");
        List<Trainee> trainees = traineeDao.findAll();
        log.info("Found {} trainees", trainees.size());
        return trainees;
    }


    private void validateTraineeInput(String firstName, String lastName,
                                      LocalDate dateOfBirth, String address) {
        if (firstName == null || firstName.trim().isEmpty()) {
            log.error("First name cannot be null or empty");
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            log.error("Last name cannot be null or empty");
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (dateOfBirth == null) {
            log.error("Date of birth cannot be null");
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            log.error("Date of birth cannot be in the future");
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
        if (address == null || address.trim().isEmpty()) {
            log.error("Address cannot be null or empty");
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
    }
}