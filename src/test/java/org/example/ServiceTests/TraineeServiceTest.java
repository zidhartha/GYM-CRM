package org.example.ServiceTests;

import com.gym.crm.Repository.TraineeRepository;
import com.gym.crm.Repository.TrainerRepository;
import com.gym.crm.Util.PasswordGenerator;
import com.gym.crm.Util.UsernameGenerator;
import com.gym.crm.dto.TraineeDto;
import com.gym.crm.dto.TraineeUpdateDto;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.TraineeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock private TraineeRepository traineeRepository;
    @Mock private TrainerRepository trainerRepository;
    @Mock private PasswordGenerator passwordGenerator;
    @Mock private UsernameGenerator usernameGenerator;

    @InjectMocks
    private TraineeService traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        User user = new User("gio","jincharadze","gio.jincharadze","Pass");
        trainee = new Trainee(user, LocalDate.of(1990,1,1),"123 Main St");
        trainee.setId(1L);
        trainee.setTrainers(new ArrayList<>());
    }

    @Test
    void createTrainee_shouldSaveAndReturnTrainee() {

        TraineeDto dto = buildDto("gio","jincharadze",
                LocalDate.of(1990,1,1),"123 Main St");

        when(passwordGenerator.generatePassword()).thenReturn("rawPass");
        when(usernameGenerator.generateUsername("gio","jincharadze"))
                .thenReturn("gio.jincharadze");

        when(traineeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Trainee result = traineeService.createTrainee(dto);

        assertThat(result.getUser().getUsername()).isEqualTo("gio.jincharadze");
        verify(traineeRepository).save(any());
    }

    @Test
    void updateTraineeProfile_shouldUpdateAddressAndDob() {

        TraineeUpdateDto dto = new TraineeUpdateDto();
        dto.setAddress("New Address");
        dto.setDateOfBirth(LocalDate.of(1995,5,15));

        when(traineeRepository.findByUserUsername("gio.jincharadze"))
                .thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        Trainee result = traineeService.updateTraineeProfile(dto,"gio.jincharadze");

        assertThat(result.getAddress()).isEqualTo("New Address");
    }

    @Test
    void updateTraineeProfile_shouldThrowWhenTraineeNotFound() {

        when(traineeRepository.findByUserUsername("ghost"))
                .thenReturn(Optional.empty());

        TraineeUpdateDto dto = new TraineeUpdateDto();

        assertThatThrownBy(() ->
                traineeService.updateTraineeProfile(dto,"ghost"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateTraineeTrainers_shouldAssignTrainers() {

        Trainer trainer1 = new Trainer(
                new User("dato","jincharadze","dato.jincharadze","pass"),
                new TrainingType("Yoga"));

        Trainer trainer2 = new Trainer(
                new User("mari","jincharadze","mari.jincharadze","pass"),
                new TrainingType("Pilates"));

        when(traineeRepository.findByUserUsername("gio.jincharadze"))
                .thenReturn(Optional.of(trainee));

        when(trainerRepository.findAllByUserUsernameIn(any()))
                .thenReturn(List.of(trainer1,trainer2));

        when(traineeRepository.save(trainee)).thenReturn(trainee);

        Trainee result = traineeService.updateTraineeTrainers(
                "gio.jincharadze",
                List.of("dato.jincharadze","mari.jincharadze"));

        assertThat(result.getTrainers())
                .containsExactlyInAnyOrder(trainer1,trainer2);
    }

    @Test
    void updateTraineeTrainers_shouldRemoveOldTrainer() {

        Trainer trainer = new Trainer(
                new User("dato","jincharadze","dato.jincharadze","pass"),
                new TrainingType("Yoga"));

        trainee.getTrainers().add(trainer);

        when(traineeRepository.findByUserUsername("gio.jincharadze"))
                .thenReturn(Optional.of(trainee));

        when(trainerRepository.findAllByUserUsernameIn(any()))
                .thenReturn(List.of());

        when(traineeRepository.save(trainee)).thenReturn(trainee);

        Trainee result = traineeService.updateTraineeTrainers(
                "gio.jincharadze",
                List.of());

        assertThat(result.getTrainers()).isEmpty();
    }

    @Test
    void updateTraineeTrainers_shouldThrowWhenTrainerMissing() {

        when(traineeRepository.findByUserUsername("gio.jincharadze"))
                .thenReturn(Optional.of(trainee));

        when(trainerRepository.findAllByUserUsernameIn(any()))
                .thenReturn(List.of());

        assertThatThrownBy(() ->
                traineeService.updateTraineeTrainers(
                        "gio.jincharadze",
                        List.of("ghost")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void selectAllTrainees_shouldReturnAllTrainees() {

        Trainee second = new Trainee(
                new User("mari","jincharadze","mari.jincharadze","pass"),
                LocalDate.of(1992,3,10),"456 Oak Ave");

        when(traineeRepository.findAll()).thenReturn(List.of(trainee,second));

        List<Trainee> result = traineeService.selectAllTrainees();

        assertThat(result).hasSize(2);
    }

    private TraineeDto buildDto(String first,String last,LocalDate dob,String address) {
        TraineeDto dto = new TraineeDto();
        dto.setFirstname(first);
        dto.setLastname(last);
        dto.setDateOfBirth(dob);
        dto.setAddress(address);
        return dto;
    }
}