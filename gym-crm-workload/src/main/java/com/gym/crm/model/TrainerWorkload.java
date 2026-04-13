package com.gym.crm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkload {
    private String username;
    private String firstName;
    private String lastName;
    private boolean active;
    private Map<Integer, Map<String, Double>> yearlySummary;

    public TrainerWorkload(String username) {
        this.username = username;
        this.yearlySummary = new HashMap<>();
    }
}
