package com.gym.crm.service;

import com.gym.crm.Repository.TrainerRepository;
import com.gym.crm.Repository.TrainingTypeRepository;
import com.gym.crm.Util.EntityMapper;
import com.gym.crm.dto.authentication.RegistrationResponseDto;
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
    private final EntityMapper entityMapper;


    @Transactional
    public RegistrationResponseDto createTrainer(@Valid TrainerCreateDto dto) {

        log.info("Creating trainer: {} {}, specialization={}",
                dto.getFirstname(),
                dto.getLastname(),
                dto.getSpecialization());
        String rawPassword = passwordGenerator.generatePassword();

        User user = new User(
                dto.getFirstname(),
                dto.getLastname(),
                usernameGenerator.generateUsername(dto.getFirstname(), dto.getLastname()),
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

        return RegistrationResponseDto.builder()
                .username(saved.getUser().getUsername())
                .password(rawPassword)
                .build();

    }

    @Transactional(readOnly = true)
    public TrainerProfileDto getTrainerByUsername(String username) {

        log.info("Fetching trainer: {}", username);

        Trainer trainer = trainerRepository.findByUserUsername(username).orElseThrow(
                () -> new IllegalArgumentException("Trainer not found: " + username)
        );

        return TrainerProfileDto.builder()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .username(trainer.getUser().getUsername())
                .specialization(trainer.getSpecialization().getName())
                .isActive(trainer.getUser().isActive())
                .trainees(entityMapper.mapToTraineeListDto(trainer.getTrainees()))
                .build();
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
        user.setActive(dto.getIsActive());
        Trainer updated = trainerRepository.save(trainer);
        return TrainerProfileDto.builder()
                .firstName(updated.getUser().getFirstName())
                .lastName(updated.getUser().getLastName())
                .username(username)
                .specialization(updated.getSpecialization().getName())
                .isActive(updated.getUser().isActive())
                .trainees(entityMapper.mapToTraineeListDto(updated.getTrainees()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<TrainerProfileDto> selectAllTrainers() {
        return trainerRepository.findAll()
                .stream()
                .map(trainer -> TrainerProfileDto.builder()
                        .firstName(trainer.getUser().getFirstName())
                        .lastName(trainer.getUser().getLastName())
                        .username(trainer.getUser().getUsername())
                        .specialization(trainer.getSpecialization().getName())
                        .isActive(trainer.getUser().isActive())
                        .trainees(entityMapper.mapToTraineeListDto(trainer.getTrainees()))
                        .build()
                )
                .toList();
    }

    public TrainerListDto getUnassignedTrainers(String username) {
        return entityMapper.mapToTrainerListDto(
                trainerRepository.findTrainersNotAssignedToTrainee(username)
        );
    }

    @Transactional(readOnly = true)
    public long countTrainers(){
        return trainerRepository.count();
    }
}