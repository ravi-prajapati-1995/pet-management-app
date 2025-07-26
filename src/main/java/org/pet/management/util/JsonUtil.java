package org.pet.management.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

public class JsonUtil {
    @Getter
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String asJsonString(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(final String body, final Class<T> type) {
        try {
            return objectMapper.readValue(body, type);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
