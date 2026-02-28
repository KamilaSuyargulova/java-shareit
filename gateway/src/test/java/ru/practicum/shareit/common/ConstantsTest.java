package ru.practicum.shareit.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    void constants_ShouldHaveCorrectValues() {
        assertEquals("X-Sharer-User-Id", Constants.HEADER_USER_ID);
    }
}