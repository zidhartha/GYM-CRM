package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.controller.WorkloadController;
import com.gym.crm.model.ActionType;
import com.gym.crm.model.TrainerWorkload;
import com.gym.crm.model.WorkloadRequest;
import com.gym.crm.service.WorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WorkloadControllerTest {

    @Mock
    private WorkloadService workloadService;

    @InjectMocks
    private WorkloadController workloadController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(workloadController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private WorkloadRequest buildRequest() {
        WorkloadRequest request = new WorkloadRequest();
        request.setTrainerUsername("john.doe");
        request.setTrainerFirstName("John");
        request.setTrainerLastName("Doe");
        request.setActive(true);
        request.setTrainingDate(LocalDate.of(2025, 3, 15));
        request.setTrainingDuration(2.0);
        request.setActionType(ActionType.ADD);
        return request;
    }

    @Test
    void shouldReturn200OnValidUpdateRequest() throws Exception {
        doNothing().when(workloadService).updateWorkload(any());

        mockMvc.perform(post("/api/workload")  // was /api/workload/update
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Transaction-Id", "test-123")
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk());

        verify(workloadService, times(1)).updateWorkload(any());
    }

    @Test
    void shouldReturn200EvenWithoutTransactionIdHeader() throws Exception {
        doNothing().when(workloadService).updateWorkload(any());

        mockMvc.perform(post("/api/workload")  // was /api/workload/update
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn200WithWorkloadDataWhenTrainerExists() throws Exception {
        TrainerWorkload workload = new TrainerWorkload("john.doe");
        workload.setFirstName("John");
        workload.setLastName("Doe");
        workload.setActive(true);

        Map<String, Double> months = new HashMap<>();
        months.put("MARCH", 2.0);
        Map<Integer, Map<String, Double>> years = new HashMap<>();
        years.put(2025, months);
        workload.setYearlySummary(years);

        when(workloadService.getWorkload("john.doe")).thenReturn(workload);

        mockMvc.perform(get("/api/workload/john.doe")
                        .header("X-Transaction-Id", "test-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void shouldReturn404WhenTrainerNotFound() throws Exception {
        when(workloadService.getWorkload("unknown")).thenReturn(null);

        mockMvc.perform(get("/api/workload/unknown")
                        .header("X-Transaction-Id", "test-123"))
                .andExpect(status().isNotFound());
    }
}