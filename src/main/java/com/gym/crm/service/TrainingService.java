package com.gym.crm.service;

import com.gym.crm.Repository.TraineeRepository;
import com.gym.crm.Repository.TrainerRepository;
import com.gym.crm.Repository.TrainingRepository;
import com.gym.crm.Repository.TrainingTypeRepository;
import com.gym.crm.dto.training.TraineeTrainingListItemDto;
import com.gym.crm.dto.training.TrainerTrainingListItemDto;
import com.gym.crm.dto.training.TrainingCreateDto;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@Service
@RequiredArgsConstructor
public class TrainingService {
    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingRepository trainingRepository;

    @Transactional
    public Training createTraining(@Valid TrainingCreateDto dto) {

        log.info("Creating training: trainee = {} , trainer = {}, type = {} , duration = {}",
                dto.getTraineeUsername(),dto.getTrainerUsername(),
                dto.getTrainingTypeName(),dto.getTrainingDuration());
        Trainee trainee = traineeRepository
                .findByUserUsername(dto.getTraineeUsername())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trainee with username " + dto.getTraineeUsername() + " not found"));

        Trainer trainer = trainerRepository
                .findByUserUsername(dto.getTrainerUsername())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trainer with username " + dto.getTrainerUsername() + " not found"));

        TrainingType trainingType = trainingTypeRepository
                .findByName(dto.getTrainingTypeName())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Training type " + dto.getTrainingTypeName() + " not found"));

        Training training = new Training(
                trainee,
                trainer,
                trainingType,
                dto.getTrainingName(),
                dto.getTrainingDate(),
                dto.getTrainingDuration()
        );

        Training saved = trainingRepository.save(training);

        log.info("Training created: id={}, trainee={}, trainer={}, type={}, name={}",
                saved.getId(),
                trainee.getUser().getUsername(),
                trainer.getUser().getUsername(),
                trainingType.getName(),
                saved.getTrainingName());

        return saved;
    }

    @Transactional(readOnly = true)
    public List<TraineeTrainingListItemDto> getTraineeTrainings(
            String traineeUsername,
            LocalDate from,
            LocalDate to,
            String trainerUsername,
            String trainingType) {

        return trainingRepository.findTraineeTrainings(
                traineeUsername, from, to, trainerUsername, trainingType).stream()
                .map(t -> new TraineeTrainingListItemDto(
                        t.getTrainingName(),
                        t.getTrainingDate(),
                        t.getTrainingType().getId(),
                        t.getTrainingDuration(),
                        t.getTrainer().getUser().getUsername()
                )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TrainerTrainingListItemDto> getTrainerTrainings(
            String trainerUsername,
            LocalDate from,
            LocalDate to,
            String traineeUsername) {

        return trainingRepository.findTrainerTrainings(
                trainerUsername, from, to, traineeUsername).stream()
                .map(t -> new TrainerTrainingListItemDto(
                        t.getTrainingName(),
                        t.getTrainingDate(),
                        t.getTrainingType().getId(),
                        t.getTrainingDuration(),
                        t.getTrainee().getUser().getUsername()
                )).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<Training> selectAllTrainings() {
        return trainingRepository.findAll();
    }
}