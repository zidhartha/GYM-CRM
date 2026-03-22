package com.gym.crm.facade;

import com.gym.crm.dto.authentication.RegistrationResponseDto;
import com.gym.crm.dto.trainee.TraineeCreateDto;
import com.gym.crm.dto.trainee.TraineeProfileDto;
import com.gym.crm.dto.trainee.TraineeUpdateDto;
import com.gym.crm.dto.trainer.TrainerCreateDto;
import com.gym.crm.dto.trainer.TrainerListDto;
import com.gym.crm.dto.trainer.TrainerProfileDto;
import com.gym.crm.dto.trainer.TrainerUpdateDto;
import com.gym.crm.dto.training.TraineeTrainingListItemDto;
import com.gym.crm.dto.training.TrainerTrainingListItemDto;
import com.gym.crm.dto.training.TrainingCreateDto;
import com.gym.crm.dto.trainingType.TrainingTypeListDto;
import com.gym.crm.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;
    private final UserService userService;


    public void authenticate(String username, String password) {
        userService.authenticate(username, password);
    }



    public void updatePassword(String username, String newPassword) {
        userService.updatePassword(username, newPassword);
    }

    public RegistrationResponseDto createTrainee(TraineeCreateDto dto) {
        return traineeService.createTrainee(dto);
    }

    public TraineeProfileDto getTraineeProfile(String username) {
        return traineeService.getTraineeProfile(username);
    }

    public TraineeProfileDto updateTraineeProfile(TraineeUpdateDto dto, String username) {
        return traineeService.updateTraineeProfile(dto, username);
    }

    public void deleteTrainee(String username) {
        traineeService.deleteTrainee(username);
    }

    public TrainerListDto updateTraineeTrainers(String username, List<String> trainerUsernames) {
        return traineeService.updateTraineeTrainers(username, trainerUsernames);
    }

    public RegistrationResponseDto createTrainer(TrainerCreateDto dto) {
        return trainerService.createTrainer(dto);
    }

    public TrainerProfileDto getTrainerProfile(String username) {
        return trainerService.getTrainerByUsername(username);
    }

    public TrainerProfileDto updateTrainer(TrainerUpdateDto dto, String username) {
        return trainerService.updateTrainer(dto, username);
    }

    public TrainerListDto getUnassignedTrainers(String username) {
        return trainerService.getUnassignedTrainers(username);
    }


    public void createTraining(TrainingCreateDto dto) {
        trainingService.createTraining(dto);
    }

    public List<TraineeTrainingListItemDto> getTraineeTrainings(
            String username, LocalDate from, LocalDate to,
            String trainerName, String trainingTypeName) {
        return trainingService.getTraineeTrainings(username, from, to, trainerName, trainingTypeName);
    }

    public List<TrainerTrainingListItemDto> getTrainerTrainings(
            String username, LocalDate from, LocalDate to, String traineeName) {
        return trainingService.getTrainerTrainings(username, from, to, traineeName);
    }


    public TrainingTypeListDto getAllTrainingTypes() {
        return trainingTypeService.getAllTrainingTypes();
    }

    public void activateUser(String username) {
        userService.activateUser(username);
    }

    public void deactivateUser(String username) {
        userService.deactivateUser(username);
    }
}