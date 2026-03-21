package com.gym.crm.dto.trainer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerListItemDto {
    private String username;

    private String firstName;

    private String lastName;

    private Long specialization;
}
