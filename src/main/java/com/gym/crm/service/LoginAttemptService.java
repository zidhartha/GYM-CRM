package com.gym.crm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LoginAttemptService {
    @Value("${auth.max-attempts}")
    private int MAX_ATTEMPTS;
    @Value("${auth.block-duration-minutes}")
    private int BLOCK_DURATION;

    private final ConcurrentHashMap<String,Integer> attempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> blockedUntil = new ConcurrentHashMap<>();

    public void loginFailed(String username){
        int currAttempt = attempts.getOrDefault(username,0) + 1;
        attempts.put(username,currAttempt);

        if(currAttempt >= MAX_ATTEMPTS){
            LocalDateTime blockUntil = LocalDateTime.now().plusMinutes(BLOCK_DURATION);
            blockedUntil.put(username,blockUntil);
            log.warn("User {} blocked until {} after {} failed attempts",
                    username,blockUntil,currAttempt);
        }
    }

    public void loginSucceeded(String username){
        attempts.remove(username);
        blockedUntil.remove(username);
    }

    public boolean isBlocked(String username){
        LocalDateTime blockUntil = blockedUntil.get(username);
        if(blockUntil == null) return false;
        if(LocalDateTime.now().isAfter(blockUntil)){
            attempts.remove(username);
            blockedUntil.remove(username);
            return false;
        }
        return true;
    }

}
