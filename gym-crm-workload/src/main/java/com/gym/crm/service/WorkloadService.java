package com.gym.crm.service;

import com.gym.crm.model.ActionType;
import com.gym.crm.model.TrainerWorkload;
import com.gym.crm.model.WorkloadRequest;
import com.gym.crm.storage.WorkloadStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadService {
    private final WorkloadStorage workloadStorage;

    public void updateWorkload(WorkloadRequest request){
        Map<String,TrainerWorkload> storage = workloadStorage.getStorage();
        TrainerWorkload workload = storage.computeIfAbsent(
                request.getTrainerUsername(), TrainerWorkload::new
        );
        syncTrainerInfo(workload, request);
        updateMonthlyHours(workload, request);

    }

    private void syncTrainerInfo(TrainerWorkload workload,WorkloadRequest request){
        workload.setUsername(request.getTrainerUsername());
        workload.setFirstName(request.getTrainerFirstName());
        workload.setLastName(request.getTrainerLastName());
        workload.setActive(request.isActive());
    }

    private void updateMonthlyHours(TrainerWorkload workload,WorkloadRequest request){
        int year = request.getTrainingDate().getYear();
        String month = request.getTrainingDate().getMonth().name();
        double duration = request.getTrainingDuration();

        Map<String, Double> monthMap = workload.getYearlySummary()
                .computeIfAbsent(year, k -> new HashMap<>());

        if (request.getActionType() == ActionType.ADD) {
            monthMap.merge(month, duration, Double::sum);
            log.info("Added {} hours for trainer {} in {}/{}", duration, workload.getUsername(), month, year);
        } else {
            monthMap.merge(month, duration, (existing, toRemove) -> {
                double result = existing - toRemove;
                return result <= 0 ? null : result;
            });
            log.info("Deleted {} hours for trainer {} in {}/{}", duration, workload.getUsername(), month, year);
        }
    }

    public TrainerWorkload getWorkload(String username){
        return workloadStorage.getStorage().get(username);
    }
}
