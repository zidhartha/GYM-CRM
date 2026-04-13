package com.gym.crm.service;

import com.gym.crm.model.User;
import com.gym.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final UserRepository userRepository;
    @Value("${auth.max-attempts}")
    private int MAX_ATTEMPTS;
    @Value("${auth.block-duration-minutes}")
    private int BLOCK_DURATION;

    @Transactional
    public void loginFailed(String username) {
        userRepository.findByUsername(username).ifPresent(
                user -> {
                    int attempts = user.getFailedAttempts() + 1;
                    user.setFailedAttempts(attempts);

                    if (attempts >= MAX_ATTEMPTS) {
                        LocalDateTime blockedUntil = LocalDateTime.now().plusMinutes(BLOCK_DURATION);
                        user.setFailedAttempts(0);
                        user.setLockedUntil(blockedUntil);
                        log.warn("User : {} has been blocked until {} after {} failed attempts.",
                                username, blockedUntil, attempts
                        );
                        userRepository.save(user);
                    }
                }
        );
    }

    @Transactional
    public void loginSucceeded(String username) {
        userRepository.findByUsername(username).ifPresent(
                user -> {
                    user.setFailedAttempts(0);
                    user.setLockedUntil(null);
                    userRepository.save(user);
                });
    }

    @Transactional
    public boolean isBlocked(String username) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) return false;
        if (user.getLockedUntil() == null) return false;

        if (LocalDateTime.now().isAfter(user.getLockedUntil())) {
            user.setLockedUntil(null);
            userRepository.save(user);
            return false;
        }

        return true;
    }
}