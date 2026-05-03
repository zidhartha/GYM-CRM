package service;

import com.gym.crm.dto.ActionType;
import com.gym.crm.model.TrainerWorkload;
import com.gym.crm.dto.WorkloadRequest;
import com.gym.crm.service.WorkloadService;
import com.gym.crm.storage.WorkloadStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceTest {

    @Mock
    private WorkloadStorage workloadStorage;

    @InjectMocks
    private WorkloadService workloadService;

    private final Map<String, TrainerWorkload> storage = new ConcurrentHashMap<>();

    @BeforeEach
    void setUp() {
        storage.clear();
        when(workloadStorage.getStorage()).thenReturn(storage);
    }

    private WorkloadRequest buildRequest(ActionType actionType, double duration, LocalDate date) {
        WorkloadRequest request = new WorkloadRequest();
        request.setTrainerUsername("john.doe");
        request.setTrainerFirstName("John");
        request.setTrainerLastName("Doe");
        request.setActive(true);
        request.setTrainingDate(date);
        request.setTrainingDuration(duration);
        request.setActionType(actionType);
        return request;
    }

    @Test
    void shouldCreateNewTrainerEntryWhenNotExists() {
        WorkloadRequest request = buildRequest(ActionType.ADD, 2.0, LocalDate.of(2025, 3, 15));

        workloadService.updateWorkload(request);

        assertThat(storage).containsKey("john.doe");
        TrainerWorkload workload = storage.get("john.doe");
        assertThat(workload.getUsername()).isEqualTo("john.doe");
        assertThat(workload.getFirstName()).isEqualTo("John");
        assertThat(workload.getLastName()).isEqualTo("Doe");
        assertThat(workload.isActive()).isTrue();
    }

    @Test
    void shouldAddHoursForNewMonth() {
        WorkloadRequest request = buildRequest(ActionType.ADD, 2.0, LocalDate.of(2025, 3, 15));

        workloadService.updateWorkload(request);

        TrainerWorkload workload = storage.get("john.doe");
        assertThat(workload.getYearlySummary())
                .containsKey(2025);
        assertThat(workload.getYearlySummary().get(2025))
                .containsEntry("MARCH", 2.0);
    }

    @Test
    void shouldAccumulateHoursForSameMonth() {
        WorkloadRequest first = buildRequest(ActionType.ADD, 2.0, LocalDate.of(2025, 3, 15));
        WorkloadRequest second = buildRequest(ActionType.ADD, 1.5, LocalDate.of(2025, 3, 20));

        workloadService.updateWorkload(first);
        workloadService.updateWorkload(second);

        TrainerWorkload workload = storage.get("john.doe");
        assertThat(workload.getYearlySummary().get(2025).get("MARCH")).isEqualTo(3.5);
    }

    @Test
    void shouldTrackMultipleMonthsSeparately() {
        WorkloadRequest march = buildRequest(ActionType.ADD, 2.0, LocalDate.of(2025, 3, 15));
        WorkloadRequest april = buildRequest(ActionType.ADD, 3.0, LocalDate.of(2025, 4, 10));

        workloadService.updateWorkload(march);
        workloadService.updateWorkload(april);

        Map<String, Double> months = storage.get("john.doe").getYearlySummary().get(2025);
        assertThat(months).containsEntry("MARCH", 2.0);
        assertThat(months).containsEntry("APRIL", 3.0);
    }

    @Test
    void shouldTrackMultipleYearsSeparately() {
        WorkloadRequest y2024 = buildRequest(ActionType.ADD, 2.0, LocalDate.of(2024, 1, 10));
        WorkloadRequest y2025 = buildRequest(ActionType.ADD, 3.0, LocalDate.of(2025, 1, 10));

        workloadService.updateWorkload(y2024);
        workloadService.updateWorkload(y2025);

        TrainerWorkload workload = storage.get("john.doe");
        assertThat(workload.getYearlySummary()).containsKey(2024);
        assertThat(workload.getYearlySummary()).containsKey(2025);
        assertThat(workload.getYearlySummary().get(2024).get("JANUARY")).isEqualTo(2.0);
        assertThat(workload.getYearlySummary().get(2025).get("JANUARY")).isEqualTo(3.0);
    }

    @Test
    void shouldSubtractHoursOnDelete() {
        WorkloadRequest add = buildRequest(ActionType.ADD, 3.0, LocalDate.of(2025, 3, 15));
        WorkloadRequest delete = buildRequest(ActionType.DELETE, 1.0, LocalDate.of(2025, 3, 15));

        workloadService.updateWorkload(add);
        workloadService.updateWorkload(delete);

        assertThat(storage.get("john.doe").getYearlySummary().get(2025).get("MARCH"))
                .isEqualTo(2.0);
    }

    @Test
    void shouldRemoveMonthEntryWhenHoursDropToZero() {
        WorkloadRequest add = buildRequest(ActionType.ADD, 2.0, LocalDate.of(2025, 3, 15));
        WorkloadRequest delete = buildRequest(ActionType.DELETE, 2.0, LocalDate.of(2025, 3, 15));

        workloadService.updateWorkload(add);
        workloadService.updateWorkload(delete);

        Map<String, Double> months = storage.get("john.doe").getYearlySummary().get(2025);
        assertThat(months).doesNotContainKey("MARCH");
    }

    @Test
    void shouldRemoveMonthEntryWhenHoursGoNegative() {
        WorkloadRequest add = buildRequest(ActionType.ADD, 1.0, LocalDate.of(2025, 3, 15));
        WorkloadRequest delete = buildRequest(ActionType.DELETE, 5.0, LocalDate.of(2025, 3, 15));

        workloadService.updateWorkload(add);
        workloadService.updateWorkload(delete);

        Map<String, Double> months = storage.get("john.doe").getYearlySummary().get(2025);
        assertThat(months).doesNotContainKey("MARCH");
    }

    @Test
    void shouldUpdateTrainerInfoOnSubsequentCalls() {
        WorkloadRequest first = buildRequest(ActionType.ADD, 1.0, LocalDate.of(2025, 3, 15));
        workloadService.updateWorkload(first);

        WorkloadRequest updated = buildRequest(ActionType.ADD, 1.0, LocalDate.of(2025, 4, 10));
        updated.setActive(false);
        workloadService.updateWorkload(updated);

        assertThat(storage.get("john.doe").isActive()).isFalse();
    }

    @Test
    void shouldReturnNullWhenTrainerNotFound() {
        TrainerWorkload result = workloadService.getWorkload("nonexistent");
        assertThat(result).isNull();
    }

    @Test
    void shouldReturnWorkloadWhenTrainerExists() {
        WorkloadRequest request = buildRequest(ActionType.ADD, 2.0, LocalDate.of(2025, 3, 15));
        workloadService.updateWorkload(request);

        TrainerWorkload result = workloadService.getWorkload("john.doe");
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john.doe");
    }
}