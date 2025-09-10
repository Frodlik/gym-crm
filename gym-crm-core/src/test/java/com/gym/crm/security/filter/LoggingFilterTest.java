package com.gym.crm.security.filter;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggingFilterTest {
    private final LoggingFilter filter = new LoggingFilter();

    @Test
    void maskSensitiveData_ShouldReplacePasswords() {
        String json = "{ \"username\":\"test\", \"password\":\"12345\", \"newPassword\":\"abcdef\", \"oldPassword\":\"qwerty\" }";

        String actual = invokeMaskSensitiveData(json);

        assertFalse(actual.contains("12345"));
        assertTrue(actual.contains("\"password\":\"****\""));
        assertTrue(actual.contains("\"newPassword\":\"****\""));
        assertTrue(actual.contains("\"oldPassword\":\"****\""));
    }

    @Test
    void truncateIfNeeded_ShouldTruncateLongString() {
        String longText = "a".repeat(1500);
        int maxLen = (int) ReflectionTestUtils.getField(filter, "MAX_PAYLOAD_LENGTH");

        String actual = invokeTruncateIfNeeded(longText);

        assertTrue(actual.endsWith("... [TRUNCATED]"));
        assertEquals(maxLen + "... [TRUNCATED]".length(), actual.length());
    }

    @Test
    void truncateIfNeeded_ShouldNotTruncateShortString() {
        String shortText = "Hello";

        String actual = invokeTruncateIfNeeded(shortText);

        assertEquals(shortText, actual);
    }

    @Test
    void toSingleLine_ShouldRemoveNewlinesAndTabs() {
        String multiLine = "{\n\t\"username\": \"test\"\n}";

        String actual = invokeToSingleLine(multiLine);

        assertEquals("{\"username\": \"test\"}", actual);
    }

    @Test
    void shouldLogRequestBody_ShouldReturnTrueForPostPutPatch() {
        assertTrue(invokeShouldLogRequestBody("POST"));
        assertTrue(invokeShouldLogRequestBody("put"));
        assertTrue(invokeShouldLogRequestBody("PaTcH"));
        assertFalse(invokeShouldLogRequestBody("GET"));
        assertFalse(invokeShouldLogRequestBody("DELETE"));
    }

    @Test
    void isSensitiveEndpoint_ShouldReturnTrueForConfiguredUris() {
        assertTrue(invokeIsSensitiveEndpoint("/api/v1/trainees/register"));
        assertTrue(invokeIsSensitiveEndpoint("/api/v1/trainers/register"));
    }

    private String invokeMaskSensitiveData(String input) {
        return ReflectionTestUtils.invokeMethod(filter, "maskSensitiveData", input);
    }

    private String invokeTruncateIfNeeded(String input) {
        return ReflectionTestUtils.invokeMethod(filter, "truncateIfNeeded", input);
    }

    private String invokeToSingleLine(String input) {
        return ReflectionTestUtils.invokeMethod(filter, "toSingleLine", input);
    }

    private boolean invokeShouldLogRequestBody(String method) {
        return ReflectionTestUtils.invokeMethod(filter, "shouldLogRequestBody", method);
    }

    private boolean invokeIsSensitiveEndpoint(String uri) {
        return ReflectionTestUtils.invokeMethod(filter, "isSensitiveEndpoint", uri);
    }
}
