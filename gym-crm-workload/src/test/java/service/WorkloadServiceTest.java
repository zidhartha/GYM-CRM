package service;

import static org.mockito.Mockito.when;


import com.gym.crm.dto.ActionType;
import com.gym.crm.dto.WorkloadRequest;
import com.gym.crm.dto.WorkloadSummaryResponse;
import com.gym.crm.model.TrainerWorkload;
import com.gym.crm.repository.TrainerWorkloadRepository;
import com.gym.crm.service.WorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @InjectMocks
    private WorkloadService workloadService;

    private WorkloadRequest request;

    @BeforeEach
    void setUp() {
        request = new WorkloadRequest();
        request.setTrainerUsername("john.doe");
        request.setTrainerFirstName("John");
        request.setTrainerLastName("Doe");
        request.setActive(true);
        request.setTrainingDate(LocalDate.of(2024, 3, 15));
        request.setTrainingDuration(60);
        request.setActionType(ActionType.ADD);
    }

    @Test
    void updateWorkload_newTrainer_createsDocumentWithCorrectDuration() {
        when(repository.findByUsername("john.doe")).thenReturn(Optional.empty());

        workloadService.updateWorkload(request);

        verify(repository).save(argThat(w ->
                w.getUsername().equals("john.doe") &&
                        w.getYearlySummary().get(0).getYear() == 2024 &&
                        w.getYearlySummary().get(0).getMonths().get(0).getMonth() == 3 &&
                        w.getYearlySummary().get(0).getMonths().get(0).getTotalDurationMinutes() == 60
        ));
    }

    @Test
    void updateWorkload_existingTrainerSameYearMonth_accumulatesDuration() {
        TrainerWorkload existing = buildWorkload("john.doe", 2024, 3, 40);
        when(repository.findByUsername("john.doe")).thenReturn(Optional.of(existing));

        workloadService.updateWorkload(request);

        verify(repository).save(argThat(w ->
                w.getYearlySummary().get(0).getMonths().get(0).getTotalDurationMinutes() == 100
        ));
    }

    @Test
    void updateWorkload_deleteAction_subtractsDuration() {
        TrainerWorkload existing = buildWorkload("john.doe", 2024, 3, 100);
        when(repository.findByUsername("john.doe")).thenReturn(Optional.of(existing));
        request.setActionType(ActionType.DELETE);

        workloadService.updateWorkload(request);

        verify(repository).save(argThat(w ->
                w.getYearlySummary().get(0).getMonths().get(0).getTotalDurationMinutes() == 40
        ));
    }

    @Test
    void updateWorkload_deleteExceedsBalance_clampsToZero() {
        TrainerWorkload existing = buildWorkload("john.doe", 2024, 3, 10);
        when(repository.findByUsername("john.doe")).thenReturn(Optional.of(existing));
        request.setActionType(ActionType.DELETE);

        workloadService.updateWorkload(request);

        verify(repository).save(argThat(w ->
                w.getYearlySummary().get(0).getMonths().get(0).getTotalDurationMinutes() == 0
        ));
    }

    @Test
    void updateWorkload_newYear_addsYearEntry() {
        TrainerWorkload existing = buildWorkload("john.doe", 2023, 3, 60);
        when(repository.findByUsername("john.doe")).thenReturn(Optional.of(existing));

        workloadService.updateWorkload(request); // date is 2024

        verify(repository).save(argThat(w -> w.getYearlySummary().size() == 2));
    }

    @Test
    void getWorkload_existingTrainer_returnsResponse() {
        TrainerWorkload existing = buildWorkload("john.doe", 2024, 3, 60);
        existing.setFirstName("John");
        existing.setLastName("Doe");
        when(repository.findByUsername("john.doe")).thenReturn(Optional.of(existing));

        WorkloadSummaryResponse response = workloadService.getWorkload("john.doe");

        assertEquals("john.doe", response.getUsername());
        assertEquals("John", response.getFirstName());
        assertFalse(response.getYearlySummary().isEmpty());
    }

    @Test
    void getWorkload_notFound_throwsNoSuchElementException() {
        when(repository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> workloadService.getWorkload("unknown"));
    }

    private TrainerWorkload buildWorkload(String username, int year, int month, int duration) {
        TrainerWorkload w = new TrainerWorkload(username);
        TrainerWorkload.YearlySummary y = new TrainerWorkload.YearlySummary(year);
        TrainerWorkload.MonthSummary m = new TrainerWorkload.MonthSummary(month);
        m.setTotalDurationMinutes(duration);
        y.getMonths().add(m);
        w.getYearlySummary().add(y);
        return w;
    }
}