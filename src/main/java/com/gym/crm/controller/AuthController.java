package com.gym.crm.controller;

import com.gym.crm.dto.authentication.ChangePasswordRequestDto;
import com.gym.crm.dto.authentication.LoginRequestDto;
import com.gym.crm.dto.authentication.LoginResponseDto;
import com.gym.crm.service.JwtService;
import com.gym.crm.service.LoginAttemptService;
import com.gym.crm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication controller")
public class AuthController {
    private final UserService userService;

    @PostMapping("/logout")
    @Operation(summary = "User logout")
    public ResponseEntity<Void> logout(){
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }


    @PutMapping("/password")
    @Operation(summary = "Change Password")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid ChangePasswordRequestDto request,
            Authentication authentication) {

        UserDetails username = (UserDetails) authentication.getPrincipal();
        userService.updatePassword(username.getUsername(),request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
