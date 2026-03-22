package com.gym.crm.dto.trainee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeListItemDto {
    private String username;
    private String firstname;
    private String lastname;
}
