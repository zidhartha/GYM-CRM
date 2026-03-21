package com.gym.crm.service;

import com.gym.crm.Repository.TrainerRepository;
import com.gym.crm.Repository.TrainingTypeRepository;
import com.gym.crm.dto.trainee.TraineeListDto;
import com.gym.crm.dto.trainer.*;
import com.gym.crm.Util.PasswordGenerator;
import com.gym.crm.Util.UsernameGenerator;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Validated
@Service
@RequiredArgsConstructor
public class TrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);
    private final PasswordGenerator passwordGenerator;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final UsernameGenerator usernameGenerator;



    @Transactional
    public Map<String,String> createTrainer(@Valid TrainerCreateDto dto) {

        log.info("Creating trainer: {} {}, specialization={}",
                dto.getFirstname(),
                dto.getLastname(),
                dto.getSpecialization());
        String rawPassword = passwordGenerator.generatePassword();

        User user = new User(
                dto.getFirstname(),
                dto.getLastname(),
                usernameGenerator.generateUsername(dto.getFirstname(),dto.getLastname()),
                rawPassword
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

        return Map.of(
                "username", saved.getUser().getUsername(),
                "password", rawPassword
        );
    }

    @Transactional(readOnly = true)
    public TrainerProfileDto getTrainerByUsername(String username) {

        log.info("Fetching trainer: {}", username);

        Trainer trainer = trainerRepository.findByUserUsername(username).orElseThrow(
                () -> new IllegalArgumentException("Trainer not found: " + username)
        );


        TraineeListDto trainees = new TraineeListDto(trainerRepository.findTrainerTrainees(username));
        return new TrainerProfileDto(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().getUsername(),
                trainer.getSpecialization().getName(),
                trainer.getUser().isActive(),
                trainees
        );
    }

    @Transactional
    public TrainerProfileDto updateTrainer(@Valid TrainerUpdateDto dto, String username) {
        log.info("Updating trainer: {}", username);

        Trainer trainer = trainerRepository.findByUserUsername(username).orElseThrow(
                () -> new IllegalArgumentException("Trainer not found: " + username)
        );


        User user = trainer.getUser();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        trainerRepository.save(trainer);
        log.info("Trainer updated: username={}, firstname={}, lastname={}",
                username,
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName());

        TraineeListDto trainees = new TraineeListDto(trainerRepository.findTrainerTrainees(username));
        return new TrainerProfileDto(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                username,
                trainer.getSpecialization().getName(),
                trainer.getUser().isActive(),
                trainees
        );
    }

    @Transactional(readOnly = true)
    public List<TrainerProfileDto> selectAllTrainers() {
        return trainerRepository.findAll()
                .stream()
                .map(t -> new TrainerProfileDto(
                        t.getUser().getUsername(),
                        t.getUser().getFirstName(),
                        t.getUser().getLastName(),
                        t.getSpecialization().getName(),
                        t.getUser().isActive(),
                        new TraineeListDto(trainerRepository.findTrainerTrainees(t.getUser().getUsername()))
                ))
                .collect(Collectors.toList());
    }

    public TrainerListDto getUnassignedTrainers(String username) {
        return new TrainerListDto(trainerRepository.findTrainersNotAssignedToTrainee(username)
                .stream()
                .map(t -> new TrainerListItemDto(
                        t.getUser().getUsername(),
                        t.getUser().getFirstName(),
                        t.getUser().getLastName(),
                        t.getSpecialization().getId()))
                .collect(Collectors.toList()));
    }
}