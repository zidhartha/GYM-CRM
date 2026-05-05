package messsaging;
import com.gym.crm.messaging.WorkloadListener;
import com.gym.crm.dto.ActionType;
import com.gym.crm.dto.WorkloadRequest;
import com.gym.crm.service.WorkloadService;
import jakarta.jms.Message;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WorkloadListenerTest {

    @Mock
    private WorkloadService workloadService;

    @Mock
    private Message rawMessage;

    @Mock
    private Validator validator;

    @InjectMocks
    private WorkloadListener workloadListener;

    private WorkloadRequest validRequest;

    @BeforeEach
    void setUp() throws Exception {
        validRequest = new WorkloadRequest();
        validRequest.setTrainerUsername("nika.cire");
        validRequest.setTrainerFirstName("Nika");
        validRequest.setTrainerLastName("Cire");
        validRequest.setActive(true);
        validRequest.setTrainingDate(LocalDate.of(2025, 4, 1));
        validRequest.setTrainingDuration(2);
        validRequest.setActionType(ActionType.ADD);

        when(rawMessage.getStringProperty("transactionId")).thenReturn("test-tx-id");
        when(validator.validate(any())).thenReturn(Collections.emptySet());
    }

    @Test
    void onMessage_validRequest_delegatesToService() throws Exception {
        workloadListener.onMessage(validRequest, rawMessage);
        verify(workloadService, times(1)).updateWorkload(validRequest);
    }

    @Test
    void onMessage_nullTrainerUsername_throwsIllegalArgument() {
        validRequest.setTrainerUsername(null);
        setupMockViolation("trainerUsername");

        assertThrows(IllegalArgumentException.class,
                () -> workloadListener.onMessage(validRequest, rawMessage));
        verify(workloadService, never()).updateWorkload(any());
    }

    @Test
    void onMessage_blankTrainerUsername_throwsIllegalArgument() {
        validRequest.setTrainerUsername("   ");
        setupMockViolation("trainerUsername");

        assertThrows(IllegalArgumentException.class,
                () -> workloadListener.onMessage(validRequest, rawMessage));
    }

    @Test
    void onMessage_nullActionType_throwsIllegalArgument() {
        validRequest.setActionType(null);
        setupMockViolation("actionType");

        assertThrows(IllegalArgumentException.class,
                () -> workloadListener.onMessage(validRequest, rawMessage));
        verify(workloadService, never()).updateWorkload(any());
    }

    @Test
    void onMessage_nullTrainingDate_throwsIllegalArgument() {
        validRequest.setTrainingDate(null);
        setupMockViolation("trainingDate");

        assertThrows(IllegalArgumentException.class,
                () -> workloadListener.onMessage(validRequest, rawMessage));
        verify(workloadService, never()).updateWorkload(any());
    }

    @Test
    void onMessage_nullTxId_stillProcessesMessage() throws Exception {
        when(rawMessage.getStringProperty("transactionId")).thenReturn(null);

        workloadListener.onMessage(validRequest, rawMessage);

        verify(workloadService, times(1)).updateWorkload(validRequest);
    }

    private void setupMockViolation(String property) {
        ConstraintViolation<WorkloadRequest> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn(property);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("invalid");
        when(validator.validate(any())).thenReturn((Set) Set.of(violation));
    }
}