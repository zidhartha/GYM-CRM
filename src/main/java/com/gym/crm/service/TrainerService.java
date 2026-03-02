package com.gym.crm.service;

import com.gym.crm.Repository.TrainerRepository;
import com.gym.crm.Repository.TrainingTypeRepository;
import com.gym.crm.dto.TrainerDto;
import com.gym.crm.dto.TrainerUpdateDto;
import com.gym.crm.Util.PasswordGenerator;
import com.gym.crm.Util.UsernameGenerator;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Validated
@Service
@RequiredArgsConstructor
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    private final PasswordGenerator passwordGenerator;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsernameGenerator usernameGenerator;
    @Transactional
    public Trainer createTrainer(@Valid TrainerDto dto) {

        log.info("Creating trainer: {} {}, specialization={}",
                dto.getFirstname(),
                dto.getLastname(),
                dto.getSpecialization());
        String rawPassword = passwordGenerator.generatePassword();
        User user = new User(
                dto.getFirstname(),
                dto.getLastname(),
                usernameGenerator.generateUsername(dto.getFirstname(),dto.getLastname()),
                passwordEncoder.encode(rawPassword)
        );
        TrainingType trainingType = trainingTypeRepository
                .findByName(dto.getSpecialization())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Training type invalid: " + dto.getSpecialization()));

        Trainer trainer = new Trainer(user, trainingType);

        Trainer saved = trainerRepository.save(trainer);

        log.info("Trainer created: id={}, username={}, specialization={}",
                saved.getId(),
                saved.getUser().getUsername(),
                saved.getSpecialization());

        return saved;
    }


    @Transactional
    public Trainer createTrainer(@Valid TrainerDto dto, String rawPassword) {
        log.info("Creating trainer with given password: {} {}, specialization={}",
                dto.getFirstname(),
                dto.getLastname(),
                dto.getSpecialization());

        User user = new User(
                dto.getFirstname(),
                dto.getLastname(),
                usernameGenerator.generateUsername(dto.getFirstname(), dto.getLastname()),
                passwordEncoder.encode(rawPassword)
        );

        System.out.println(">>> Trainer " + dto.getFirstname() + "." + dto.getLastname() + " : password = " + rawPassword);

        TrainingType trainingType = trainingTypeRepository
                .findByName(dto.getSpecialization())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Training type invalid: " + dto.getSpecialization()));

        Trainer trainer = new Trainer(user, trainingType);

        Trainer saved = trainerRepository.save(trainer);

        log.info("Trainer created: id={}, username={}, specialization={}",
                saved.getId(),
                saved.getUser().getUsername(),
                saved.getSpecialization());

        return saved;
    }



    @Transactional(readOnly = true)
    public Optional<Trainer> getTrainerByUsername(String username) {

        log.info("Fetching trainer: {}", username);

        return trainerRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainers(String username) {

        log.info("Fetching unassigned trainers for trainee: {}", username);

        return trainerRepository.findTrainersNotAssignedToTrainee(username);
    }

    @Transactional
    public Trainer updateTrainer(@Valid TrainerUpdateDto dto, String username) {

        log.info("Updating trainer: {}", username);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException(
                        "Trainer with username " + username + " not found"));

        if (dto.getSpecialization() != null) {

            TrainingType specialization = trainingTypeRepository
                    .findByName(dto.getSpecialization())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Training type not found: " + dto.getSpecialization()));

            trainer.setSpecialization(specialization);
        }

        Trainer updated = trainerRepository.save(trainer);

        log.info("Trainer updated: username={}, specialization={}",
                updated.getUser().getUsername(),
                updated.getSpecialization());

        return updated;
    }

    @Transactional(readOnly = true)
    public List<Trainer> selectAllTrainers() {
        return trainerRepository.findAll();
    }
}