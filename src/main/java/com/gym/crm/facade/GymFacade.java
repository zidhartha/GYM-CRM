package com.gym.crm.facade;
import com.gym.crm.dto.LoginRequestDto;
import com.gym.crm.dto.TraineeDto;
import com.gym.crm.dto.TraineeUpdateDto;
import com.gym.crm.dto.TrainerDto;
import com.gym.crm.dto.TrainerUpdateDto;
import com.gym.crm.dto.TrainingDto;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserService userService;


    public void authenticate(String username, String password) {
        log.info("Authenticating user: {}", username);
        LoginRequestDto credentials = new LoginRequestDto(username, password);
        userService.authenticate(credentials);
    }

    public Trainee createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        log.info("Facade: createTrainee {} {}", firstName, lastName);
        TraineeDto dto = new TraineeDto();
        dto.setFirstname(firstName);
        dto.setLastname(lastName);
        dto.setDateOfBirth(dateOfBirth);
        dto.setAddress(address);
        return traineeService.createTrainee(dto);
    }

    public Trainee createTraineeWithPassword(String firstName,String lastName, LocalDate dateOfBirth, String address,String password){
        log.info("Facade: createTrainee {} {}", firstName, lastName);
        TraineeDto dto = new TraineeDto();
        dto.setFirstname(firstName);
        dto.setLastname(lastName);
        dto.setDateOfBirth(dateOfBirth);
        dto.setAddress(address);
        return traineeService.createTrainee(dto,password);
    }

    public Trainee selectTrainee(String authenticatedUsername, String password, String targetUsername) {
        authenticate(authenticatedUsername, password);
        log.info("Facade: selectTrainee {}", targetUsername);
        return traineeService.getTraineeByUsername(targetUsername);
    }

    public Trainee updateTrainee(String username, String password, String address, LocalDate dateOfBirth) {
        authenticate(username, password);
        log.info("Facade: updateTrainee {}", username);
        TraineeUpdateDto updateDto = new TraineeUpdateDto();
        updateDto.setAddress(address);
        updateDto.setDateOfBirth(dateOfBirth);
        return traineeService.updateTraineeProfile(updateDto, username);
    }


    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        authenticate(username, oldPassword);
        log.info("Facade: changeTraineePassword {}", username);
        userService.updatePassword(username, newPassword);
    }


    public void toggleTraineeActive(String username, String password) {
        authenticate(username, password);
        log.info("Facade: toggleTraineeActive {}", username);
        userService.updateActiveStatus(username);
    }


    public void deleteTrainee(String username, String password) {
        authenticate(username, password);
        log.info("Facade: deleteTrainee {}", username);
        traineeService.deleteTrainee(username);
    }


    public boolean matchTraineeCredentials(String username, String password) {
        log.info("Facade: matchTraineeCredentials {}", username);
        try {
            authenticate(username, password);
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("Trainee credential mismatch for username: {}", username);
            return false;
        }
    }


    public List<Training> getTraineeTrainings(String username, String password,
                                              LocalDate from, LocalDate to,
                                              String trainerUsername, String trainingType) {
        authenticate(username, password);
        log.info("Facade: getTraineeTrainings for {}", username);
        return trainingService.getTraineeTrainings(username, from, to, trainerUsername, trainingType);
    }


    public List<Trainer> getUnassignedTrainers(String username, String password) {
        authenticate(username, password);
        log.info("Facade: getUnassignedTrainers for trainee {}", username);
        return trainerService.getUnassignedTrainers(username);
    }


    public Trainee updateTraineeTrainers(String username, String password, List<String> trainerUsernames) {
        authenticate(username, password);
        log.info("Facade: updateTraineeTrainers for {}", username);
        return traineeService.updateTraineeTrainers(username, trainerUsernames);
    }


    public Trainer createTrainer(String firstName, String lastName, String specialization) {
        log.info("Facade: createTrainer {} {}", firstName, lastName);
        TrainerDto dto = new TrainerDto();
        dto.setFirstname(firstName);
        dto.setLastname(lastName);
        dto.setSpecialization(specialization);
        return trainerService.createTrainer(dto);
    }


    public Trainer createTrainerWithPassword(String firstName, String lastName, String specialization,String password){
        log.info("Facade: createTrainer {} {}", firstName, lastName);
        TrainerDto dto = new TrainerDto();
        dto.setFirstname(firstName);
        dto.setLastname(lastName);
        dto.setSpecialization(specialization);
        return trainerService.createTrainer(dto,password);
    }


    public Trainer selectTrainer(String authenticatedUsername, String password, String targetUsername) {
        authenticate(authenticatedUsername, password);
        log.info("Facade: selectTrainer {}", targetUsername);
        return trainerService.getTrainerByUsername(targetUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + targetUsername));
    }


    public Trainer updateTrainer(String username, String password, String specialization) {
        authenticate(username, password);
        log.info("Facade: updateTrainer {}", username);
        TrainerUpdateDto updateDto = new TrainerUpdateDto();
        updateDto.setSpecialization(specialization);
        return trainerService.updateTrainer(updateDto, username);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        authenticate(username, oldPassword);
        log.info("Facade: changeTrainerPassword {}", username);
        userService.updatePassword(username, newPassword);
    }

    public void toggleTrainerActive(String username, String password) {
        authenticate(username, password);
        log.info("Facade: toggleTrainerActive {}", username);
        userService.updateActiveStatus(username);
    }
    public boolean matchTrainerCredentials(String username, String password) {
        log.info("Facade: matchTrainerCredentials {}", username);
        try {
            authenticate(username, password);
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("Trainer credential mismatch for username: {}", username);
            return false;
        }
    }

    public List<Training> getTrainerTrainings(String username, String password,
                                              LocalDate from, LocalDate to,
                                              String traineeUsername) {
        authenticate(username, password);
        log.info("Facade: getTrainerTrainings for {}", username);
        return trainingService.getTrainerTrainings(username, from, to, traineeUsername);
    }

    public Training createTraining(String traineeUsername, String traineePassword,
                                   String trainerUsername, String trainingName,
                                   String trainingTypeName, LocalDate trainingDate,
                                   Long durationMinutes) {
        authenticate(traineeUsername, traineePassword);
        log.info("Facade: createTraining trainee={} trainer={}", traineeUsername, trainerUsername);
        TrainingDto dto = new TrainingDto();
        dto.setTraineeUsername(traineeUsername);
        dto.setTrainerUsername(trainerUsername);
        dto.setTrainingName(trainingName);
        dto.setTrainingTypeName(trainingTypeName);
        dto.setTrainingDate(trainingDate);
        dto.setTrainingDuration(durationMinutes);
        return trainingService.createTraining(dto);
    }


    public List<Trainee> selectAllTrainees() {
        return traineeService.selectAllTrainees();
    }

    public List<Trainer> selectAllTrainers() {
        return trainerService.selectAllTrainers();
    }

    public List<Training> selectAllTrainings() {
        return trainingService.selectAllTrainings();
    }
}