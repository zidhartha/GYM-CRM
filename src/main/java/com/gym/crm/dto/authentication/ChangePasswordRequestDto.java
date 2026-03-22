package com.gym.crm.dto.authentication;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequestDto {
    @NotBlank(message="Username can not be blank")
    private String username;

    @NotBlank(message="Password can not be blank")
    private String password;

    @NotBlank(message = "New password can not be blank")
    private String newPassword;
}
