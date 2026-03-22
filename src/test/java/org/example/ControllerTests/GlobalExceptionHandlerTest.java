package org.example.ControllerTests;

import com.gym.crm.controller.GlobalExceptionHandler;
import com.gym.crm.dto.error.ErrorResponseDto;
import com.gym.crm.exceptions.AccessDeniedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalState_shouldReturn409() {
        IllegalStateException ex = new IllegalStateException("conflict occurred");

        ResponseEntity<ErrorResponseDto> response = handler.handleIllegalState(ex);

        assertEquals(409, response.getStatusCode().value());
        assertEquals(409, response.getBody().getStatusCode());
        assertEquals("conflict occurred", response.getBody().getErrorMessage());
    }

    @Test
    void handleGeneric_shouldReturn500() {
        Exception ex = new Exception("something went wrong");

        ResponseEntity<ErrorResponseDto> response = handler.handleGeneric(ex);

        assertEquals(500, response.getStatusCode().value());
        assertEquals(500, response.getBody().getStatusCode());
        assertEquals("Internal server error", response.getBody().getErrorMessage());
    }

    @Test
    void handleAccessDenied_shouldReturn401() {
        AccessDeniedException ex = new AccessDeniedException("invalid credentials");

        ResponseEntity<ErrorResponseDto> response = handler.handleAccessDenied(ex);

        assertEquals(401, response.getStatusCode().value());
        assertEquals(401, response.getBody().getStatusCode());
        assertEquals("invalid credentials", response.getBody().getErrorMessage());
    }

    @Test
    void handleIllegalArgument_shouldReturn400() {
        IllegalArgumentException ex = new IllegalArgumentException("bad input");

        ResponseEntity<ErrorResponseDto> response = handler.handleIllegalArgument(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals("bad input", response.getBody().getErrorMessage());
    }

    @Test
    void handleValidation_shouldReturn400WithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("obj", "firstName", "must not be blank");
        FieldError fieldError2 = new FieldError("obj", "lastName", "must not be blank");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<ErrorResponseDto> response = handler.handleValidation(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals(400, response.getBody().getStatusCode());
        assertTrue(response.getBody().getErrorMessage().contains("firstName: must not be blank"));
        assertTrue(response.getBody().getErrorMessage().contains("lastName: must not be blank"));
    }

    @Test
    void handleValidation_shouldReturn400WithSingleFieldError() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("obj", "username", "must not be blank");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponseDto> response = handler.handleValidation(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("username: must not be blank", response.getBody().getErrorMessage());
    }
}
