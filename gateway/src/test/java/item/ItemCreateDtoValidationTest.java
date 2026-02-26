package item;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.Item.dto.ItemCreateDto;

import static org.junit.jupiter.api.Assertions.*;

class ItemCreateDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .requestId(1L)
                .build();

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenNameIsValid_thenNoViolations() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Valid Name")
                .description("Power drill")
                .available(true)
                .requestId(1L)
                .build();

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenDescriptionIsValid_thenNoViolations() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Drill")
                .description("Valid description")
                .available(true)
                .requestId(1L)
                .build();

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenAvailableIsNull_thenViolations() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(null)
                .requestId(1L)
                .build();

        var violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Available cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void whenAvailableIsTrue_thenNoViolations() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .requestId(1L)
                .build();

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenAvailableIsFalse_thenNoViolations() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(false)
                .requestId(1L)
                .build();

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenRequestIdIsNegative_thenViolations() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .requestId(-1L)
                .build();

        var violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Request id cannot be negative", violations.iterator().next().getMessage());
    }

    @Test
    void whenRequestIdIsZero_thenViolations() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .requestId(0L)
                .build();

        var violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Request id cannot be negative", violations.iterator().next().getMessage());
    }

    @Test
    void whenRequestIdIsPositive_thenNoViolations() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .requestId(5L)
                .build();

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenRequestIdIsNull_thenNoViolations() {
        ItemCreateDto dto = ItemCreateDto.builder()
                .name("Drill")
                .description("Power drill")
                .available(true)
                .requestId(null)
                .build();

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}