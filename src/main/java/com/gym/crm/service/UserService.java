package com.gym.crm.service;

import com.gym.crm.Repository.UserRepository;
import com.gym.crm.dto.authentication.LoginRequestDto;
import com.gym.crm.exceptions.AccessDeniedException;
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
        if (!userRepository.existsByUsernameAndPassword(credentials.getUsername(), credentials.getPassword())) {
            throw new AccessDeniedException("Invalid credentials for user: " + credentials.getUsername());
        }
    }

    public void authenticate(String username,String password){
            log.info("Authenticating user {}",username);
            if(!userRepository.existsByUsernameAndPassword(username,password)){
                throw new AccessDeniedException("Invalid credentials for user: " + username);
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
    public void activateUser(String username){
        log.info("Activating active status for user : {}",username);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        if (user.isActive()) {
            throw new IllegalStateException("User " + username + " is already active.");
        }
        user.setActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(String username){
        log.info("Deactivating active status for user : {}",username);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        if (!user.isActive()) {
            throw new IllegalStateException("User " + username + " is already inactive.");
        }
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public long countUsers(){
        return userRepository.count();
    }
}
