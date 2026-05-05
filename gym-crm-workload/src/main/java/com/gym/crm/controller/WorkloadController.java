package com.gym.crm.controller;

import com.gym.crm.dto.WorkloadSummaryResponse;
import com.gym.crm.model.TrainerWorkload;
import com.gym.crm.dto.WorkloadRequest;
import com.gym.crm.service.WorkloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    public ResponseEntity<Void> updateWorkload(@Valid @RequestBody WorkloadRequest request) {
        log.info("[txId={}] Received workload update for trainer: {}, action: {}",
                MDC.get("transactionId"), request.getTrainerUsername(), request.getActionType());
        workloadService.updateWorkload(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get trainer workload summary")
    @GetMapping("/{username}")
    public ResponseEntity<WorkloadSummaryResponse> getWorkload(@PathVariable String username) {
        log.info("[txId={}] Fetching workload for trainer: {}",
                MDC.get("transactionId"), username);
        return ResponseEntity.ok(workloadService.getWorkload(username));
    }

}