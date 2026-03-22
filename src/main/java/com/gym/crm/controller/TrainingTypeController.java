package com.gym.crm.controller;

import com.gym.crm.dto.trainingType.TrainingTypeListDto;
import com.gym.crm.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/training-types")
@RequiredArgsConstructor
@Tag(name="Training type controller")
public class TrainingTypeController {
        private final TrainingTypeService trainingTypeService;

    @GetMapping
    @Operation(summary = "Get all training types")
    public ResponseEntity<TrainingTypeListDto> getAllTrainingTypes() {
        return ResponseEntity.ok(trainingTypeService.getAllTrainingTypes());
    }

}
