package com.gym.crm.controller;

import com.gym.crm.dto.trainee.TraineeCreateDto;
import com.gym.crm.dto.trainee.TraineeProfileDto;
import com.gym.crm.dto.trainee.TraineeUpdateDto;
import com.gym.crm.dto.trainer.TrainerListDto;
import com.gym.crm.dto.training.TraineeTrainingListItemDto;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainees")
@Tag(name="trainee controller")
public class TraineeController {
    private final TraineeService traineeService;
    private final UserService userService;
    private final TrainingService trainingService;
    private final TrainerService trainerService;

    @PostMapping
    @Operation(summary = "Create a trainee")
    public ResponseEntity<Map<String,String>> createTrainee(@RequestBody @Valid TraineeCreateDto traineeCreateDto){
        return ResponseEntity.status(201).body(traineeService.createTrainee(traineeCreateDto));
    }

    @GetMapping("/{username}")
    @Operation(summary="Get the trainee profile")
    public ResponseEntity<TraineeProfileDto> getTraineeProfile(
            @RequestHeader ("X-Username") String authUsername,
            @RequestHeader ("X-Password") String authPassword,
            @PathVariable String username
    ){
        userService.authenticate(authUsername,authPassword);
        userService.assertIdentity(authUsername,username);

        return ResponseEntity.ok(traineeService.getTraineeProfile(username));
    }

    @PutMapping("/{username}")
    @Operation(summary="Update the trainee profile")
    public ResponseEntity<TraineeProfileDto> updateTraineeProfile(
            @RequestHeader ("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @PathVariable String username,
            @RequestBody @Valid TraineeUpdateDto traineeUpdateDto
    ){
        userService.authenticate(authUsername,authPassword);
        userService.assertIdentity(authUsername,username);
        return ResponseEntity.ok(traineeService.updateTraineeProfile(traineeUpdateDto,authUsername));
    }

    @PatchMapping("/{username}")
    @Operation(summary = "Activate/Deactivate trainee")
    public ResponseEntity<Void> activateDeactivateTrainee(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @PathVariable String username,
            @RequestParam boolean isActive
    ){
        userService.authenticate(authUsername,authPassword);
        userService.assertIdentity(authUsername,username);

        if(!isActive) {
            userService.deactivateUser(username);
        }else {
            userService.activateUser(username);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete trainee profile")
    public ResponseEntity<Void> deleteTraineeProfile(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @PathVariable String username
    ){
        userService.authenticate(authUsername,authPassword);
        userService.assertIdentity(authUsername,username);


        traineeService.deleteTrainee(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainee's trainings list")
    public ResponseEntity<List<TraineeTrainingListItemDto>> getTraineeTrainings(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @PathVariable("username") String username,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingTypeName) {

        userService.authenticate(authUsername, authPassword);
        userService.assertIdentity(authUsername, username);

        return ResponseEntity.ok(trainingService.getTraineeTrainings(username, from, to, trainerName, trainingTypeName));
    }

    @GetMapping("/{username}/unassigned-trainers")
    @Operation(summary = "Get non-assigned active trainers for a trainee")
    public ResponseEntity<TrainerListDto> getNotAssignedTrainers(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @PathVariable String username) {

        userService.authenticate(authUsername, authPassword);
        userService.assertIdentity(authUsername, username);

        return ResponseEntity.ok(trainerService.getUnassignedTrainers(username));
    }

    @PutMapping("/{username}/trainers")
    @Operation(summary="Update trainee's trainer list")
    public ResponseEntity<TrainerListDto> updateTraineeTrainers(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @PathVariable("username") String username,
            @RequestBody List<String> trainerUsernames
            ){
        userService.authenticate(authUsername, authPassword);
        userService.assertIdentity(authUsername, username);

        return ResponseEntity.ok(traineeService.updateTraineeTrainers(username,trainerUsernames));
    }
}