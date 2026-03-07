package org.example.ServiceTests;

import com.gym.crm.Repository.TrainerRepository;
import com.gym.crm.Repository.TrainingTypeRepository;
import com.gym.crm.Util.PasswordGenerator;
import com.gym.crm.Util.UsernameGenerator;
import com.gym.crm.dto.TrainerDto;
import com.gym.crm.dto.TrainerUpdateDto;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {
    @Mock private TrainerRepository trainerRepository;
    @Mock private TrainingTypeRepository trainingTypeRepository;
    @Mock private PasswordGenerator passwordGenerator;
    @Mock private UsernameGenerator usernameGenerator;
    @InjectMocks private TrainerService trainerService;

    private Trainer trainer;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType("Yoga");
        trainingType.setId(1L);
        User user = new User("dato", "jincharadze", "dato.jincharadze", "encodedPass");
        trainer = new Trainer(user, trainingType);
        trainer.setId(1L);
    }

    @Test
    void createTrainer_shouldSaveAndReturnTrainer() {
        TrainerDto dto = buildDto("dato", "jincharadze", "Yoga");

        TrainingType trainingType = new TrainingType(1L, "Yoga");

        when(passwordGenerator.generatePassword()).thenReturn("rawPass");
        when(usernameGenerator.generateUsername("dato", "jincharadze"))
                .thenReturn("dato.jincharadze");
        when(trainingTypeRepository.findByName("Yoga"))
                .thenReturn(Optional.of(trainingType));

        when(trainerRepository.save(any(Trainer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.createTrainer(dto);

        assertThat(result.getUser().getUsername()).isEqualTo("dato.jincharadze");
        assertThat(result.getUser().getPassword()).isEqualTo("rawPass");
        assertThat(result.getSpecialization().getName()).isEqualTo("Yoga");

        verify(trainingTypeRepository).findByName("Yoga");
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void createTrainer_shouldThrowWhenTrainingTypeNotFound() {
        TrainerDto dto = buildDto("dato", "jincharadze", "InvalidType");
        when(trainingTypeRepository.findByName("InvalidType")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.createTrainer(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("InvalidType");
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void getTrainerByUsername_shouldReturnTrainer() {
        when(trainerRepository.findByUsername("dato.jincharadze")).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = trainerService.getTrainerByUsername("dato.jincharadze");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo("dato.jincharadze");
    }

    @Test
    void getTrainerByUsername_shouldReturnEmptyWhenNotFound() {
        when(trainerRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.getTrainerByUsername("ghost");
        assertThat(result).isEmpty();
    }

    // ----- UPDATE -----
    @Test
    void updateTrainer_shouldUpdateSpecialization() {
        TrainingType newType = new TrainingType("Pilates");
        newType.setId(2L);
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setSpecialization("Pilates");

        when(trainerRepository.findByUsername("dato.jincharadze")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByName("Pilates")).thenReturn(Optional.of(newType));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        Trainer result = trainerService.updateTrainer(dto, "dato.jincharadze");

        assertThat(result.getSpecialization().getName()).isEqualTo("Pilates");
    }

    @Test
    void updateTrainer_shouldThrowWhenTrainerNotFound() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setSpecialization("Yoga");
        when(trainerRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.updateTrainer(dto, "ghost"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("ghost");
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void updateTrainer_shouldThrowWhenNewTrainingTypeNotFound() {
        TrainerUpdateDto dto = new TrainerUpdateDto();
        dto.setSpecialization("InvalidType");
        when(trainerRepository.findByUsername("dato.jincharadze")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByName("InvalidType")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainerService.updateTrainer(dto, "dato.jincharadze"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("InvalidType");
        verify(trainerRepository, never()).save(any());
    }

    // ----- UNASSIGNED TRAINERS -----
    @Test
    void getUnassignedTrainers_shouldReturnTrainersNotAssignedToTrainee() {
        Trainer second = new Trainer(new User("gio", "jincharadze", "gio.jincharadze", "pass"), new TrainingType("Pilates"));
        when(trainerRepository.findTrainersNotAssignedToTrainee("gio.jincharadze"))
                .thenReturn(List.of(trainer, second));

        List<Trainer> result = trainerService.getUnassignedTrainers("gio.jincharadze");
        assertThat(result).hasSize(2).containsExactlyInAnyOrder(trainer, second);
    }

    @Test
    void getUnassignedTrainers_shouldReturnEmptyWhenAllAssigned() {
        when(trainerRepository.findTrainersNotAssignedToTrainee("gio.jincharadze")).thenReturn(List.of());
        List<Trainer> result = trainerService.getUnassignedTrainers("gio.jincharadze");
        assertThat(result).isEmpty();
    }

    // ----- SELECT ALL -----
    @Test
    void selectAllTrainers_shouldReturnAll() {
        Trainer second = new Trainer(new User("gio", "jincharadze", "gio.jincharadze", "pass"), new TrainingType("Pilates"));
        when(trainerRepository.findAll()).thenReturn(List.of(trainer, second));

        List<Trainer> result = trainerService.selectAllTrainers();
        assertThat(result).hasSize(2);
    }

    @Test
    void selectAllTrainers_shouldReturnEmptyList() {
        when(trainerRepository.findAll()).thenReturn(List.of());
        List<Trainer> result = trainerService.selectAllTrainers();
        assertThat(result).isEmpty();
    }

    private TrainerDto buildDto(String first, String last, String specialization) {
        TrainerDto dto = new TrainerDto();
        dto.setFirstname(first);
        dto.setLastname(last);
        dto.setSpecialization(specialization);
        return dto;
    }
}