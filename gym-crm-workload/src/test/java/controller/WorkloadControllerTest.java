package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.controller.WorkloadController;
import com.gym.crm.dto.ActionType;
import com.gym.crm.dto.WorkloadRequest;
import com.gym.crm.dto.WorkloadSummaryResponse;
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
import java.util.List;
import java.util.NoSuchElementException;

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
        mockMvc = MockMvcBuilders.standaloneSetup(workloadController)
                .setControllerAdvice(new NoSuchElementExceptionHandler()) // see below
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private WorkloadRequest buildRequest() {
        WorkloadRequest request = new WorkloadRequest();
        request.setTrainerUsername("john.doe");
        request.setTrainerFirstName("John");
        request.setTrainerLastName("Doe");
        request.setActive(true);
        request.setTrainingDate(LocalDate.of(2024, 3, 15));
        request.setTrainingDuration(60);
        request.setActionType(ActionType.ADD);
        return request;
    }

    private WorkloadSummaryResponse buildResponse() {
        return WorkloadSummaryResponse.builder()
                .username("john.doe")
                .firstName("John")
                .lastName("Doe")
                .active(true)
                .yearlySummary(List.of())
                .build();
    }

    @Test
    void updateWorkload_validRequest_returns200() throws Exception {
        doNothing().when(workloadService).updateWorkload(any());

        mockMvc.perform(post("/api/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Transaction-Id", "test-123")
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk());

        verify(workloadService, times(1)).updateWorkload(any());
    }

    @Test
    void updateWorkload_noTransactionIdHeader_stillReturns200() throws Exception {
        doNothing().when(workloadService).updateWorkload(any());

        mockMvc.perform(post("/api/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void getWorkload_existingTrainer_returnsResponseBody() throws Exception {
        when(workloadService.getWorkload("john.doe")).thenReturn(buildResponse());

        mockMvc.perform(get("/api/workload/john.doe")
                        .header("X-Transaction-Id", "test-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void getWorkload_unknownTrainer_returns404() throws Exception {
        when(workloadService.getWorkload("unknown"))
                .thenThrow(new NoSuchElementException("No workload data for trainer: unknown"));

        mockMvc.perform(get("/api/workload/unknown")
                        .header("X-Transaction-Id", "test-123"))
                .andExpect(status().isNotFound());
    }

    // Minimal exception handler so NoSuchElementException maps to 404
    @org.springframework.web.bind.annotation.RestControllerAdvice
    static class NoSuchElementExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(NoSuchElementException.class)
        public org.springframework.http.ResponseEntity<String> handle(NoSuchElementException ex) {
            return org.springframework.http.ResponseEntity.notFound().build();
        }
    }
}