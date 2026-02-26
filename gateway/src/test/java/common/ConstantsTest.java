package common;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.common.Constants;

import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    void constants_ShouldHaveCorrectValues() {
        assertEquals("X-Sharer-User-Id", Constants.HEADER_USER_ID);
    }
}