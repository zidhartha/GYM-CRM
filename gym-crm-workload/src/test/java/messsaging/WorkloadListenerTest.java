package messsaging;

import com.gym.crm.messaging.WorkloadListener;
import com.gym.crm.model.ActionType;
import com.gym.crm.model.WorkloadRequest;
import com.gym.crm.service.WorkloadService;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WorkloadListenerTest {

    @Mock
    private WorkloadService workloadService;

    @Mock
    private Message rawMessage;

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
        validRequest.setTrainingDuration(2.0);
        validRequest.setActionType(ActionType.ADD);

        when(rawMessage.getStringProperty("transactionId")).thenReturn("test-tx-id");
    }


    @Test
    void onMessage_validRequest_delegatesToService() throws Exception {
        workloadListener.onMessage(validRequest, rawMessage);

        verify(workloadService, times(1)).updateWorkload(validRequest);
    }

    @Test
    void onMessage_validRequest_doesNotThrow() throws Exception {
        workloadListener.onMessage(validRequest, rawMessage);
        // no exception = pass
    }


    @Test
    void onMessage_nullTrainerUsername_throwsIllegalArgument() throws Exception {
        validRequest.setTrainerUsername(null);

        assertThrows(IllegalArgumentException.class,
                () -> workloadListener.onMessage(validRequest, rawMessage));
    }

    @Test
    void onMessage_blankTrainerUsername_throwsIllegalArgument() throws Exception {
        validRequest.setTrainerUsername("   ");

        assertThrows(IllegalArgumentException.class,
                () -> workloadListener.onMessage(validRequest, rawMessage));
    }

    @Test
    void onMessage_nullTrainerUsername_neverCallsService() throws Exception {
        validRequest.setTrainerUsername(null);

        assertThrows(IllegalArgumentException.class,
                () -> workloadListener.onMessage(validRequest, rawMessage));

        verify(workloadService, never()).updateWorkload(any());
    }


    @Test
    void onMessage_nullActionType_throwsIllegalArgument() throws Exception {
        validRequest.setActionType(null);

        assertThrows(IllegalArgumentException.class,
                () -> workloadListener.onMessage(validRequest, rawMessage));
    }

    @Test
    void onMessage_nullActionType_neverCallsService() throws Exception {
        validRequest.setActionType(null);

        assertThrows(IllegalArgumentException.class,
                () -> workloadListener.onMessage(validRequest, rawMessage));

        verify(workloadService, never()).updateWorkload(any());
    }


    @Test
    void onMessage_nullTrainingDate_throwsIllegalArgument() throws Exception {
        validRequest.setTrainingDate(null);

        assertThrows(IllegalArgumentException.class,
                () -> workloadListener.onMessage(validRequest, rawMessage));
    }

    @Test
    void onMessage_nullTrainingDate_neverCallsService() throws Exception {
        validRequest.setTrainingDate(null);

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



}