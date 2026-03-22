package com.gym.crm.service;

import com.gym.crm.Repository.TraineeRepository;
import com.gym.crm.Repository.TrainerRepository;
import com.gym.crm.Util.EntityMapper;
import com.gym.crm.Util.PasswordGenerator;
import com.gym.crm.Util.UsernameGenerator;
import com.gym.crm.dto.authentication.RegistrationResponseDto;
import com.gym.crm.dto.trainee.TraineeCreateDto;
import com.gym.crm.dto.trainee.TraineeListDto;
import com.gym.crm.dto.trainee.TraineeProfileDto;
import com.gym.crm.dto.trainee.TraineeUpdateDto;
import com.gym.crm.dto.trainer.TrainerListDto;
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
import java.util.*;
import java.util.stream.Collectors;

@Validated
@Service
@RequiredArgsConstructor
public class TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);
    private final PasswordGenerator passwordGenerator;
    private final TraineeRepository traineeRepository;
    private final UsernameGenerator usernameGenerator;
    private final TrainerRepository trainerRepository;
    private final EntityMapper entityMapper;

    @Transactional
    public RegistrationResponseDto createTrainee(@Valid TraineeCreateDto dto) {
        log.info("Creating trainee: {} {}, dob={}, address={}",
                dto.getFirstname(),
                dto.getLastname(),
                dto.getDateOfBirth(),
                dto.getAddress());
        String rawPassword = passwordGenerator.generatePassword();
        User user = new User(
                dto.getFirstname(),
                dto.getLastname(),
                usernameGenerator.generateUsername(dto.getFirstname(), dto.getLastname()),
                rawPassword
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

        return RegistrationResponseDto.builder()
                .username(saved.getUser().getUsername())
                .password(rawPassword)
                .build();
    }

    @Transactional
    public TraineeProfileDto updateTraineeProfile(@Valid TraineeUpdateDto dto, String username) {
        log.info("Updating trainee: {}", username);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trainee with username " + username + " not found"));
        User user = trainee.getUser();

        trainee.setAddress(dto.getAddress());
        trainee.setDateOfBirth(dto.getDateOfBirth());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setActive(dto.isActive());
        Trainee updated = traineeRepository.save(trainee);
        log.info("Trainee updated: username={}, address={}, dob={},firstname={}, lastname={}",
                updated.getUser().getUsername(),
                updated.getAddress(),
                updated.getDateOfBirth(),
                updated.getUser().getFirstName(),
                updated.getUser().getLastName()
        );

        return TraineeProfileDto.builder()
                .firstName(updated.getUser().getFirstName())
                .lastName(updated.getUser().getLastName())
                .dateOfBirth(updated.getDateOfBirth())
                .address(updated.getAddress())
                .isActive(updated.getUser().isActive())
                .trainers(entityMapper.mapToTrainerListDto(updated.getTrainers()))
                .build();
    }

    @Transactional(readOnly = true)
    public TraineeProfileDto getTraineeProfile(String username) {
        log.info("Fetching trainee: {}", username);

        Trainee trainee = traineeRepository.findByUserUsername(username).orElseThrow(
                () -> new IllegalArgumentException("Trainee not found: " + username)
        );

        return TraineeProfileDto.builder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.getUser().isActive())
                .trainers(entityMapper.mapToTrainerListDto(trainee.getTrainers()))
                .build();
    }

    @Transactional
    public TrainerListDto updateTraineeTrainers(String username, List<String> trainerUsernames) {
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
        if (newTrainers.size() != newTrainerNames.size()) {
            throw new IllegalArgumentException("Some trainers are not found.");
        }

        // remove every trainer from this trainee if they are not in the new trainer usernames.
        Set<String> requestedTrainerSet = new HashSet<>(trainerUsernames);
        trainee.getTrainers().removeIf(t -> !requestedTrainerSet.contains(t.getUser().getUsername()));
        trainee.getTrainers().addAll(newTrainers);

        Trainee updated = traineeRepository.save(trainee);

        log.info("Trainee {} trainers updated: {}", username, trainerUsernames);
        return entityMapper.mapToTrainerListDto(updated.getTrainers());
    }

    @Transactional
    public void deleteTrainee(String username) {
        log.info("Deleting trainee: {}", username);
        traineeRepository.deleteByUserUsername(username);
        log.info("Trainee deleted: {}", username);
    }

    @Transactional(readOnly = true)
    public TraineeListDto selectAllTrainees() {
        return entityMapper.mapToTraineeListDto(traineeRepository.findAll());
    }
    }


