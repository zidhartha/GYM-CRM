package com.gym.crm.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.repository.TrainerRepository;
import org.springframework.transaction.annotation.Transactional;


@Component
public class UsernameGenerator {
    private static final Logger log = LoggerFactory.getLogger(UsernameGenerator.class);

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    public UsernameGenerator(TrainerRepository trainerRepository,
                             TraineeRepository traineeRepository) {
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
    }

    @Transactional()
    public String generateUsername(String firstname, String lastname) {
        String base = (firstname.trim() + "." + lastname.trim()).toLowerCase();
        String username = base;
        int counter = 1;

        Set<String> existingUsernames = getAllUsernames();

        while (existingUsernames.contains(username)) {
            username = base + counter;
            counter++;
            log.debug("Username {} already exists, trying {}", base, username);
        }

        log.debug("Generated username {}", username);
        return username;
    }

    private Set<String> getAllUsernames() {
        Set<String> usernames = new HashSet<>();

        usernames.addAll(trainerRepository.findAllUsernames());
        usernames.addAll(traineeRepository.findAllUsernames());

        return usernames;
    }
}