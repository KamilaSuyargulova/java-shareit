package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFound_ShouldReturnNotFoundWithMessage() {
        String message = "Item not found";
        NotFoundException exception = new NotFoundException(message);

        ErrorResponse response = errorHandler.handleNotFound(exception);

        assertNotNull(response);
        assertEquals(message, response.getError());
    }

    @Test
    void handleConflict_ShouldReturnConflictWithMessage() {
        String message = "Email already exists";
        ConflictException exception = new ConflictException(message);

        ErrorResponse response = errorHandler.handleConflict(exception);

        assertNotNull(response);
        assertEquals(message, response.getError());
    }

    @Test
    void handleException_ShouldReturnInternalServerError() {
        String message = "Unexpected error occurred";
        Exception exception = new RuntimeException(message);

        ErrorResponse response = errorHandler.handleException(exception);

        assertNotNull(response);
        assertEquals("Internal server error: " + message, response.getError());
    }

    @Test
    void handleException_WithNullMessage_ShouldHandleGracefully() {
        Exception exception = new RuntimeException();

        ErrorResponse response = errorHandler.handleException(exception);

        assertNotNull(response);
        assertTrue(response.getError().contains("Internal server error:"));
    }
}