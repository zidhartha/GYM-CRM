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
    public ResponseEntity<Void> updateWorkload(
            @RequestBody WorkloadRequest request,
            @RequestHeader(value = "X-Transaction-Id",required = false) String transactionId
            ){
        MDC.put("transactionId", transactionId != null ? transactionId : "none");
        log.info("[TRANSACTION] Received workload update for trainer: {}, action: {}",
                request.getTrainerUsername(), request.getActionType());

        workloadService.updateWorkload(request);

        log.info("[TRANSACTION] Workload update completed for trainer: {}", request.getTrainerUsername());
        MDC.clear();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get trainer workload summary")
    @GetMapping("/{username}")
    public ResponseEntity<TrainerWorkload> getWorkload(
            @PathVariable String username,
            @RequestHeader(value = "X-Transaction-Id",required = false) String transactionId
    ){
        MDC.put("transactionId", transactionId != null ? transactionId : "none");
        log.info("[TRANSACTION] Fetching workload summary for trainer: {}", username);

        TrainerWorkload workload = workloadService.getWorkload(username);

        if (workload == null) {
            log.warn("[TRANSACTION] No workload data found for trainer: {}", username);
            MDC.clear();
            return ResponseEntity.notFound().build();
        }

        log.info("[TRANSACTION] Returning workload summary for trainer: {}", username);
        MDC.clear();
        return ResponseEntity.ok(workload);
    }
}
