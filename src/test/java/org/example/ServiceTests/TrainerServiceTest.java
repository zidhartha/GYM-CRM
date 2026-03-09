package org.example.ServiceTests;

import com.gym.crm.Repository.TrainerRepository;
import com.gym.crm.Repository.TrainingTypeRepository;
import com.gym.crm.Util.PasswordGenerator;
import com.gym.crm.Util.UsernameGenerator;
import com.gym.crm.dto.TrainerDto;
import com.gym.crm.dto.TrainerUpdateDto;
import com.gym.crm.model.*;

import com.gym.crm.service.TrainerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private TrainingTypeRepository trainingTypeRepository;
    @Mock private PasswordGenerator passwordGenerator;
    @Mock private UsernameGenerator usernameGenerator;

    @InjectMocks
    private TrainerService trainerService;

    private Trainer trainer;
    private TrainingType yoga;

    @BeforeEach
    void setUp() {

        yoga = new TrainingType("Yoga");

        User user = new User("dato","jincharadze","dato.jincharadze","pass");
        trainer = new Trainer(user,yoga);
    }

    @Test
    void createTrainer_shouldCreateTrainer() {

        TrainerDto dto = TrainerDto.builder()
                .firstname("dato")
                .lastname("jincharadze")
                .specialization("Yoga")
                .build();

        when(usernameGenerator.generateUsername("dato","jincharadze"))
                .thenReturn("dato.jincharadze");

        when(passwordGenerator.generatePassword()).thenReturn("pass");

        when(trainingTypeRepository.findByName("Yoga"))
                .thenReturn(Optional.of(yoga));

        when(trainerRepository.save(any())).thenReturn(trainer);

        Trainer result = trainerService.createTrainer(dto);

        assertThat(result).isNotNull();
    }

    @Test
    void createTrainer_shouldThrowIfTrainingTypeInvalid() {

        TrainerDto dto = TrainerDto.builder()
                .firstname("dato")
                .lastname("jincharadze")
                .specialization("Invalid")
                .build();

        when(trainingTypeRepository.findByName("Invalid"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                trainerService.createTrainer(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getTrainerByUsername_shouldReturnTrainer() {

        when(trainerRepository.findByUserUsername("dato.jincharadze"))
                .thenReturn(Optional.of(trainer));

        Optional<Trainer> result =
                trainerService.getTrainerByUsername("dato.jincharadze");

        assertThat(result).isPresent();
    }

    @Test
    void updateTrainer_shouldUpdateSpecialization() {

        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setFirstName("new");
        dto.setLastName("name");
        dto.setSpecialization("Yoga");

        when(trainerRepository.findByUserUsername("dato.jincharadze"))
                .thenReturn(Optional.of(trainer));

        when(trainingTypeRepository.findByName("Yoga"))
                .thenReturn(Optional.of(yoga));

        when(trainerRepository.save(trainer)).thenReturn(trainer);

        Trainer result = trainerService.updateTrainer(dto,"dato.jincharadze");

        assertThat(result.getUser().getFirstName()).isEqualTo("new");
    }
}