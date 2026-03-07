package com.gym.crm.service;

import com.gym.crm.Repository.UserRepository;
import com.gym.crm.dto.LoginRequestDto;
import com.gym.crm.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public void authenticate(LoginRequestDto credentials) {
        log.info("Authenticating user: {}",credentials.getUsername());
        User user = userRepository.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + credentials.getUsername()));

        if (!credentials.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
    }

    @Transactional
    public void updatePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    @Transactional
    public void updateActiveStatus(String username) {
        log.info("Toggling active status for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        user.setActive(!user.isActive());
        userRepository.save(user);
        log.info("Active status toggled for user: {} -> now active={}", username, user.isActive());
    }


    @Transactional
    public void activateUser(String username){
        log.info("Activating active status for user : {}",username);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        user.setActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(String username){
        log.info("Deactivating active status for user : {}",username);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        user.setActive(false);
        userRepository.save(user);
    }
}
