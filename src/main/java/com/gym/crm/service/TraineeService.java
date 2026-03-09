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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trainee with username " + username + " not found"));
        User user = trainee.getUser();

        trainee.setAddress(dto.getAddress());
        trainee.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getPassword() != null) user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        Trainee updated = traineeRepository.save(trainee);
        log.info("Trainee updated: username={}, address={}, dob={},firstname={}, lastname={}",
                updated.getUser().getUsername(),
                updated.getAddress(),
                updated.getDateOfBirth(),
                updated.getUser().getFirstName(),
                updated.getUser().getLastName()
                );

        return updated;
    }

    @Transactional(readOnly = true)
    public Trainee getTraineeByUsername(String username) {
        log.info("Fetching trainee: {}", username);
        return traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + username));
    }

    @Transactional
    public Trainee updateTraineeTrainers(String username, List<String> trainerUsernames) {
        log.info("Updating trainers list for trainee: {}", username);

        // first i get the trainee with trainers
        Trainee trainee = traineeRepository.findByUserUsername(username).
                orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + username));

        Set<String> currentTrainerUsernames = trainee.getTrainers().
                stream().map(t -> t.getUser().getUsername()).
                collect(Collectors.toSet());

        // determine which of the usernames i must add to the trainee trainers.
        Set<String> newTrainerNames = trainerUsernames.stream().filter(
                name -> !currentTrainerUsernames.contains(name))
                .collect(Collectors.toSet());
        // Second query for getting the trainer objects of the new trainers.
        List<Trainer> newTrainers = trainerRepository.findAllByUserUsernameIn(newTrainerNames);
        if(newTrainers.size() != newTrainerNames.size()){
            throw new IllegalArgumentException("Some trainers are not found.");
        }

        // remove every trainer from this trainee if they are not in the new trainer usernames.
        Set<String> requestedTrainerSet = new HashSet<>(trainerUsernames);
        trainee.getTrainers().removeIf(t -> !requestedTrainerSet.contains(t.getUser().getUsername()));

        trainee.getTrainers().addAll(newTrainers);

        Trainee updated = traineeRepository.save(trainee);

        log.info("Trainee {} trainers updated: {}", username, trainerUsernames);
        return updated;
    }

    @Transactional
    public void deleteTrainee(String username) {
        log.info("Deleting trainee: {}", username);
        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + username));
        traineeRepository.delete(trainee);
        log.info("Trainee deleted: {}", username);
    }

    @Transactional(readOnly = true)
    public List<Trainee> selectAllTrainees() {
        return traineeRepository.findAll();
    }
}