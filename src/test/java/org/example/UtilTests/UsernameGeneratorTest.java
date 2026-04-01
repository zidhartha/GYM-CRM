package org.example.UtilTests;

import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsernameGeneratorTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private TraineeRepository traineeRepository;

    @InjectMocks private UsernameGenerator usernameGenerator;

    @Test
    void generateUsername_shouldReturnBaseUsername_whenNoConflict() {
        when(trainerRepository.findAllUsernames()).thenReturn(List.of());
        when(traineeRepository.findAllUsernames()).thenReturn(List.of());

        String result = usernameGenerator.generateUsername("Gio", "Janelidze");

        assertEquals("gio.janelidze", result);
    }

    @Test
    void generateUsername_shouldAppendCounter_whenUsernameExists() {
        when(trainerRepository.findAllUsernames()).thenReturn(List.of("gio.janelidze"));
        when(traineeRepository.findAllUsernames()).thenReturn(List.of());

        String result = usernameGenerator.generateUsername("Gio", "Janelidze");

        assertEquals("gio.janelidze1", result);
    }

    @Test
    void generateUsername_shouldIncrementCounter_whenMultipleConflicts() {
        when(trainerRepository.findAllUsernames()).thenReturn(List.of("gio.janelidze"));
        when(traineeRepository.findAllUsernames()).thenReturn(List.of("gio.janelidze1"));

        String result = usernameGenerator.generateUsername("Gio", "Janelidze");

        assertEquals("gio.janelidze2", result);
    }

    @Test
    void generateUsername_shouldTrimWhitespace() {
        when(trainerRepository.findAllUsernames()).thenReturn(List.of());
        when(traineeRepository.findAllUsernames()).thenReturn(List.of());

        String result = usernameGenerator.generateUsername("  Gio  ", "  Janelidze  ");

        assertEquals("gio.janelidze", result);
    }

    @Test
    void generateUsername_shouldBeLowercase() {
        when(trainerRepository.findAllUsernames()).thenReturn(List.of());
        when(traineeRepository.findAllUsernames()).thenReturn(List.of());

        String result = usernameGenerator.generateUsername("GIO", "JANELIDZE");

        assertEquals("gio.janelidze", result);
    }
}