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
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {
    @Mock private TraineeRepository traineeRepository;
    @Mock private TrainerRepository trainerRepository;
    @Mock private PasswordGenerator passwordGenerator;
    @Mock private UsernameGenerator usernameGenerator;
    @InjectMocks private TraineeService traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        User user = new User("gio", "jincharadze", "gio.jincharadze", "encodedPass");
        trainee = new Trainee(user, LocalDate.of(1990, 1, 1), "123 Main St");
        trainee.setId(1L);
    }

    @Test
    void createTrainee_shouldSaveAndReturnTrainee() {
        TraineeDto dto = buildDto("gio", "jincharadze",
                LocalDate.of(1990, 1, 1), "123 Main St");

        when(passwordGenerator.generatePassword()).thenReturn("rawPass");
        when(usernameGenerator.generateUsername("gio", "jincharadze"))
                .thenReturn("gio.jincharadze");

        when(traineeRepository.save(any(Trainee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Trainee result = traineeService.createTrainee(dto);

        assertThat(result.getUser().getUsername()).isEqualTo("gio.jincharadze");
        assertThat(result.getUser().getPassword()).isEqualTo("rawPass");
        assertThat(result.getAddress()).isEqualTo("123 Main St");

        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void updateTraineeProfile_shouldUpdateAddressAndDob() {
        TraineeUpdateDto dto = new TraineeUpdateDto();
        dto.setAddress("New Address");
        dto.setDateOfBirth(LocalDate.of(1995, 5, 15));

        when(traineeRepository.findByUserUsername("gio.jincharadze")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        Trainee result = traineeService.updateTraineeProfile(dto, "gio.jincharadze");

        assertThat(result.getAddress()).isEqualTo("New Address");
        assertThat(result.getDateOfBirth()).isEqualTo(LocalDate.of(1995, 5, 15));
    }


    @Test
    void getTraineeByUsername_shouldReturnTrainee() {
        when(traineeRepository.findByUserUsername("gio.jincharadze")).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.getTraineeByUsername("gio.jincharadze");

        assertThat(result).isEqualTo(trainee);
    }

    @Test
    void deleteTrainee_shouldFindAndDeleteTrainee() {
        when(traineeRepository.findByUserUsername("gio.jincharadze")).thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee("gio.jincharadze");

        verify(traineeRepository).delete(trainee);
    }

    @Test
    void updateTraineeTrainers_shouldAssignTrainers() {
        Trainer trainer1 = new Trainer(new User("dato", "jincharadze", "dato.jincharadze", "pass"), new TrainingType("Yoga"));
        Trainer trainer2 = new Trainer(new User("mari", "jincharadze", "mari.jincharadze", "pass"), new TrainingType("Pilates"));

        when(traineeRepository.findByUserUsername("gio.jincharadze")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("dato.jincharadze")).thenReturn(Optional.of(trainer1));
        when(trainerRepository.findByUserUsername("mari.jincharadze")).thenReturn(Optional.of(trainer2));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        Trainee result = traineeService.updateTraineeTrainers("gio.jincharadze",
                List.of("dato.jincharadze", "mari.jincharadze"));

        assertThat(result.getTrainers()).containsExactlyInAnyOrder(trainer1, trainer2);
    }

    @Test
    void selectAllTrainees_shouldReturnAllTrainees() {
        Trainee second = new Trainee(
                new User("mari", "jincharadze", "mari.jincharadze", "pass"),
                LocalDate.of(1992, 3, 10), "456 Oak Ave");

        when(traineeRepository.findAll()).thenReturn(List.of(trainee, second));

        List<Trainee> result = traineeService.selectAllTrainees();

        assertThat(result).hasSize(2);
    }

    private TraineeDto buildDto(String first, String last, LocalDate dob, String address) {
        TraineeDto dto = new TraineeDto();
        dto.setFirstname(first);
        dto.setLastname(last);
        dto.setDateOfBirth(dob);
        dto.setAddress(address);
        return dto;
    }
}