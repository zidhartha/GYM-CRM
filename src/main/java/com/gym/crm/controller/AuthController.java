package com.gym.crm.controller;

import com.gym.crm.dto.authentication.ChangePasswordRequestDto;
import com.gym.crm.dto.authentication.LoginRequestDto;
import com.gym.crm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication controller")
public class AuthController {
    private final UserService userService;

    @GetMapping("/login")
    @Operation(summary = "User Login")
    public ResponseEntity<Void> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {
        userService.authenticate(username, password);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    @Operation(summary = "Change Password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequestDto request) {
        userService.authenticate(new LoginRequestDto(request.getUsername(), request.getPassword()));
        userService.updatePassword(request.getUsername(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
