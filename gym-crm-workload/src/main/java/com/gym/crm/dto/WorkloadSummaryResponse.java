package com.gym.crm.dto;

import com.gym.crm.model.TrainerWorkload.YearlySummary;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class WorkloadSummaryResponse {
    private String username;
    private String firstName;
    private String lastName;
    private boolean active;
    private List<YearlySummary> yearlySummary;
}