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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication controller")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;

    @PostMapping("/login")
    @Operation(summary = "User Login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody @Valid LoginRequestDto request
            ) {
        if(loginAttemptService.isBlocked(request.getUsername())){
            return ResponseEntity.status(429)
                    .body(new LoginResponseDto(
                            null,
                            "User is blocked due to many failed attempts.Try again in 5 minutes"
                    ));
        }
        try{
            userService.authenticate(request);
            loginAttemptService.loginSucceeded(request.getUsername());
            String token = jwtService.generateToken(request.getUsername());
            return ResponseEntity.ok(new LoginResponseDto(token,"Login successful"));
        }catch(Exception e){
            loginAttemptService.loginFailed(request.getUsername());
            return ResponseEntity.status(401).body(
                    new LoginResponseDto(null,"Invalid credentials")
            );
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout")
    public ResponseEntity<Void> logout(){
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }


    @PutMapping("/password")
    @Operation(summary = "Change Password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequestDto request) {
        userService.updatePassword(request.getUsername(), request.getPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
