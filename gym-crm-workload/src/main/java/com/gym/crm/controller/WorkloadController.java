package com.gym.crm.controller;

import com.gym.crm.model.TrainerWorkload;
import com.gym.crm.model.WorkloadRequest;
import com.gym.crm.service.WorkloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workload")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Workload Management")
public class WorkloadController {
    private final WorkloadService workloadService;

    @Operation(summary = "Update trainer workload")
    @PostMapping
    public ResponseEntity<Void> updateWorkload(@RequestBody WorkloadRequest request) {
        log.info("Received workload update for trainer: {}, action: {}",
                request.getTrainerUsername(), request.getActionType());
        workloadService.updateWorkload(request);
        log.info("Workload update completed for trainer: {}", request.getTrainerUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get trainer workload summary")
    @GetMapping("/{username}")
    public ResponseEntity<TrainerWorkload> getWorkload(@PathVariable String username) {
        log.info("Fetching workload summary for trainer: {}", username);
        TrainerWorkload workload = workloadService.getWorkload(username);
        if (workload == null) {
            log.warn("No workload data found for trainer: {}", username);
            return ResponseEntity.notFound().build();
        }
        log.info("Returning workload summary for trainer: {}", username);
        return ResponseEntity.ok(workload);
    }
}
