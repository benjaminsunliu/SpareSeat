package com.soen345.ticketreserve.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnBadRequestMessage() {
        Map<String, String> response = handler.handleBadRequest(new BadRequestException("Invalid input"));

        assertEquals(Map.of("error", "Invalid input"), response);
    }

    @Test
    void shouldReturnDuplicateOrInvalidDataMessage() {
        Map<String, String> response = handler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("duplicate key")
        );

        assertEquals(Map.of("error", "Duplicate or invalid data"), response);
    }

    @Test
    void shouldReturnGenericErrorMessage() {
        Map<String, String> response = handler.handleGeneric(new RuntimeException("boom"));

        assertEquals(Map.of("error", "Unexpected server error"), response);
    }
}
