package com.gym.crm.controller;

import com.gym.crm.dto.authentication.ActivationDto;
import com.gym.crm.dto.authentication.RegistrationResponseDto;
import com.gym.crm.dto.trainer.TrainerCreateDto;
import com.gym.crm.dto.trainer.TrainerProfileDto;
import com.gym.crm.dto.trainer.TrainerUpdateDto;
import com.gym.crm.dto.training.TrainerTrainingListItemDto;
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


@RestController
@RequiredArgsConstructor
@RequestMapping("/trainers")
@Tag(name="Trainer management")
public class TrainerController {
    private final TrainerService trainerService;
    private final UserService userService;
    private final TrainingService trainingService;
    @PostMapping
    @Operation(summary="Creating a trainer")
    public ResponseEntity<RegistrationResponseDto> createTrainer(
            @RequestBody TrainerCreateDto trainerCreateDto
            ){
        return ResponseEntity.status(201).body(trainerService.createTrainer(trainerCreateDto));
    }

    @GetMapping("/{username}")
    @Operation(summary = "Fetching a trainer")
    public ResponseEntity<TrainerProfileDto> getTrainer(
            @PathVariable("username") String username
    ){


        return ResponseEntity.status(200).body(trainerService.getTrainerByUsername(username));
    }

    @PutMapping("/{username}")
    @Operation(summary = "Updating a trainer")
    public ResponseEntity<TrainerProfileDto> updateTrainer(
            @PathVariable("username") String username,
            @RequestBody @Valid TrainerUpdateDto trainerUpdateDto
            ){


        return ResponseEntity.ok().body(trainerService.updateTrainer(trainerUpdateDto,username));
    }

    @PatchMapping("/{username}")
    @Operation(summary = "Activate/Deactivate trainer")
    public ResponseEntity<Void> activateDeactivateTrainer(
            @PathVariable String username,
            @RequestBody  ActivationDto activationDto
            ){

        if(!activationDto.getIsActive()) {
            userService.deactivateUser(username);
        }else {
            userService.activateUser(username);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainer's trainings list")
    public ResponseEntity<List<TrainerTrainingListItemDto>> getTrainerTrainings(
            @PathVariable("username") String username,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) String traineeName) {

        return ResponseEntity.ok(trainingService.getTrainerTrainings(username, from, to, traineeName));
    }
}
