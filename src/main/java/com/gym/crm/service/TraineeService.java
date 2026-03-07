package com.gym.crm.service;

import com.gym.crm.Repository.TraineeRepository;
import com.gym.crm.Repository.TrainerRepository;
import com.gym.crm.Util.PasswordGenerator;
import com.gym.crm.Util.UsernameGenerator;
import com.gym.crm.dto.TraineeDto;
import com.gym.crm.dto.TraineeUpdateDto;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@RequiredArgsConstructor
@Service
public class TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);
    private final PasswordGenerator passwordGenerator;
    private final TraineeRepository traineeRepository;
    private final UsernameGenerator usernameGenerator;
    private final TrainerRepository trainerRepository;
    @Transactional
    public Trainee createTrainee(@Valid TraineeDto dto) {
        log.info("Creating trainee: {} {}, dob={}, address={}",
                dto.getFirstname(),
                dto.getLastname(),
                dto.getDateOfBirth(),
                dto.getAddress());

        User user = new User(
                dto.getFirstname(),
                dto.getLastname(),
                usernameGenerator.generateUsername(dto.getFirstname(),dto.getLastname()),
                passwordGenerator.generatePassword()
        );

        Trainee trainee = new Trainee(
                user,
                dto.getDateOfBirth(),
                dto.getAddress()
        );

        Trainee saved = traineeRepository.save(trainee);

        log.info("Trainee created: id={}, username={}",
                saved.getId(),
                saved.getUser().getUsername());

        return saved;
    }

    @Transactional
    public Trainee updateTraineeProfile(@Valid TraineeUpdateDto dto, String username) {

        log.info("Updating trainee: {}", username);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trainee with username " + username + " not found"));
            trainee.setAddress(dto.getAddress());
            trainee.setDateOfBirth(dto.getDateOfBirth());

        Trainee updated = traineeRepository.save(trainee);

        log.info("Trainee updated: username={}, address={}, dob={}",
                updated.getUser().getUsername(),
                updated.getAddress(),
                updated.getDateOfBirth());

        return updated;
    }

    @Transactional(readOnly = true)
    public Trainee getTraineeByUsername(String username) {
        log.info("Fetching trainee: {}", username);
        return traineeRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + username));
    }

    @Transactional
    public Trainee updateTraineeTrainers(String username, List<String> trainerUsernames) {
        log.info("Updating trainers list for trainee: {}", username);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + username));

        List<Trainer> trainers = trainerUsernames.stream()
                .map(trainerUsername -> trainerRepository.findByUsername(trainerUsername)
                        .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + trainerUsername)))
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

        trainee.setTrainers(trainers);

        Trainee updated = traineeRepository.save(trainee);

        log.info("Trainee {} trainers updated: {}", username, trainerUsernames);

        return updated;
    }

    @Transactional
    public void deleteTrainee(String username) {
        log.info("Deleting trainee: {}", username);
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + username));
        traineeRepository.delete(trainee);
        log.info("Trainee deleted: {}", username);
    }

    @Transactional(readOnly = true)
    public List<Trainee> selectAllTrainees() {
        return traineeRepository.findAll();
    }
}