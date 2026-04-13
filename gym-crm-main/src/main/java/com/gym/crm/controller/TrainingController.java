package com.gym.crm.controller;

import com.gym.crm.dto.training.TrainingCreateDto;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainings")
@Tag(name = "Training management")
public class TrainingController {
    private final TrainingService trainingService;

    @PostMapping
    @Operation(summary = "Add a new training")
    public ResponseEntity<Void> createTraining(
            @RequestBody @Valid TrainingCreateDto trainingCreateDto
            ) {

        trainingService.createTraining(trainingCreateDto);
        return ResponseEntity.ok().build();
    }
}
