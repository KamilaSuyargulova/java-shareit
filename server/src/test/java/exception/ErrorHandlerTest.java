package exception;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleBadRequest_ShouldReturnBadRequestWithMessage() {
        String message = "Invalid request data";
        BadRequestException exception = new BadRequestException(message);

        ErrorResponse response = errorHandler.handleBadRequest(exception);

        assertNotNull(response);
        assertEquals(message, response.getError());
    }

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
    void handleValidationException_ShouldReturnBadRequestWithValidationMessage() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "must not be null");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals("Validation failed: must not be null", response.getError());
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