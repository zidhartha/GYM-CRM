package com.gym.crm.service;

import com.gym.crm.dto.ActionType;
import com.gym.crm.dto.WorkloadSummaryResponse;
import com.gym.crm.model.TrainerWorkload;
import com.gym.crm.dto.WorkloadRequest;
import com.gym.crm.repository.TrainerWorkloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import com.gym.crm.model.TrainerWorkload.MonthSummary;
import com.gym.crm.model.TrainerWorkload.YearlySummary;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadService {

    private final TrainerWorkloadRepository repository;

    public void updateWorkload(WorkloadRequest request) {
        String txId = MDC.get("transactionId");
        log.info("[txId={}] processWorkload START action={} trainer={}",
                txId, request.getActionType(), request.getTrainerUsername());

        int year  = request.getTrainingDate().getYear();
        int month = request.getTrainingDate().getMonthValue();

        //if deleting an non existant object, it must not create a redundant entry in my database.
        if (request.getActionType() == ActionType.DELETE) {
            repository.findByUsername(request.getTrainerUsername())
                    .ifPresent(workload -> {
                        workload.getYearlySummary().stream()
                                .filter(y -> y.getYear() == year)
                                .findFirst()
                                .flatMap(y -> y.getMonths().stream()
                                        .filter(m -> m.getMonth() == month)
                                        .findFirst())
                                .ifPresent(m -> {
                                    updateDuration(m, request, txId, year, month);
                                    repository.save(workload);
                                });
                    });
            return;
        }
        TrainerWorkload workload = repository.findByUsername(request.getTrainerUsername())
                .orElseGet(() -> createNewWorkload(request, txId));

        updateTrainerInfo(workload, request);

        YearlySummary yearlySummary = findOrCreateYear(workload, year, txId);
        MonthSummary  monthSummary  = findOrCreateMonth(yearlySummary, month, txId);

        updateDuration(monthSummary, request, txId, year, month);

        repository.save(workload);
        log.info("[txId={}] processWorkload END trainer={} year={} month={} duration={}",
                txId, request.getTrainerUsername(), year, month,
                monthSummary.getTotalDurationMinutes());
    }

    private void updateTrainerInfo(TrainerWorkload workload, WorkloadRequest request) {
        workload.setFirstName(request.getTrainerFirstName());
        workload.setLastName(request.getTrainerLastName());
        workload.setActive(request.isActive());
    }

    private void updateDuration(MonthSummary monthSummary, WorkloadRequest request,
                                String txId, int year, int month) {
        int prev    = monthSummary.getTotalDurationMinutes();
        int deltaUpdate   = request.getActionType() == ActionType.ADD
                        ? request.getTrainingDuration()
                        : -request.getTrainingDuration();
        int updated = Math.max(0, prev + deltaUpdate);

        log.debug("[txId={}] duration update: year={} month={} prev={} delta update={} next={}",
                txId, year, month, prev, deltaUpdate, updated);

        monthSummary.setTotalDurationMinutes(updated);
    }

    private TrainerWorkload createNewWorkload(WorkloadRequest request, String txId) {
        log.debug("[txId={}] No existing record for trainer={}, creating new document",
                txId, request.getTrainerUsername());
        TrainerWorkload w = new TrainerWorkload(request.getTrainerUsername());
        updateTrainerInfo(w, request);
        return w;
    }

    private YearlySummary findOrCreateYear(TrainerWorkload workload, int year, String txId) {
        if(year < 1950){
            throw new IllegalArgumentException("Invalid year value: " + year);
        }
        return workload.getYearlySummary().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    log.debug("[txId={}] Creating year entry year={}", txId, year);
                    YearlySummary y = new YearlySummary(year);
                    workload.getYearlySummary().add(y);
                    return y;
                });
    }

    private MonthSummary findOrCreateMonth(YearlySummary yearlySummary, int month, String txId) {
        if(month < 1 || month > 12){
            throw new IllegalArgumentException("Invalid month value: " + month);
        }
        return yearlySummary.getMonths().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                    log.debug("[txId={}] Creating month entry month={}", txId, month);
                    MonthSummary m = new MonthSummary(month);
                    yearlySummary.getMonths().add(m);
                    return m;
                });
    }

    public WorkloadSummaryResponse getWorkload(String username) {
        String txId = MDC.get("transactionId");
        log.info("[txId={}] getWorkload username={}", txId, username);
        return repository.findByUsername(username)
                .map(w -> WorkloadSummaryResponse.builder()
                        .username(w.getUsername())
                        .firstName(w.getFirstName())
                        .lastName(w.getLastName())
                        .active(w.isActive())
                        .yearlySummary(w.getYearlySummary())
                        .build())
                .orElseThrow(() -> new NoSuchElementException(
                        "No workload data for trainer: " + username));
    }
}