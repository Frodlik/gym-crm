package com.gym.crm.workloadservice.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggingFilterTest {
    private final LoggingFilter filter = new LoggingFilter();

    @Test
    void truncate_ShouldTruncateLongString() {
        String longText = "a".repeat(1500);
        int maxLen = (int) ReflectionTestUtils.getField(filter, "MAX_PAYLOAD_LENGTH");

        String actual = invokeTruncate(longText);

        assertTrue(actual.endsWith("... [TRUNCATED]"));
        assertEquals(maxLen + "... [TRUNCATED]".length(), actual.length());
    }

    @Test
    void truncate_ShouldNotTruncateShortString() {
        String shortText = "Hello";

        String actual = invokeTruncate(shortText);

        assertEquals(shortText, actual);
    }

    private String invokeTruncate(String input) {
        return ReflectionTestUtils.invokeMethod(filter, "truncate", input);
    }
}