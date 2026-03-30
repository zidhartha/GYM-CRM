package com.gym.crm.util;

import com.gym.crm.dto.trainee.TraineeListDto;
import com.gym.crm.dto.trainee.TraineeListItemDto;
import com.gym.crm.dto.trainer.TrainerListDto;
import com.gym.crm.dto.trainer.TrainerListItemDto;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class EntityMapper {

    public TrainerListDto mapToTrainerListDto(Collection<Trainer> trainers) {
        return new TrainerListDto(
                trainers.stream()
                        .map(trainer -> TrainerListItemDto.builder()
                                .username(trainer.getUser().getUsername())
                                .firstName(trainer.getUser().getFirstName())
                                .lastName(trainer.getUser().getLastName())
                                .specialization(trainer.getSpecialization().getId())
                                .build()
                        )
                        .toList()
        );
    }

    public TraineeListDto mapToTraineeListDto(Collection<Trainee> trainees) {
        return new TraineeListDto(
                trainees.stream()
                        .map(trainee -> TraineeListItemDto.builder()
                                .username(trainee.getUser().getUsername())
                                .firstname(trainee.getUser().getFirstName())
                                .lastname(trainee.getUser().getLastName())
                                .build()
                        )
                        .toList()
        );
    }
}