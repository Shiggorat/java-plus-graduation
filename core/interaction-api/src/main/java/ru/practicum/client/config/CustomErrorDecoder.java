package ru.practicum.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import ru.practicum.errors.ApiError;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.errors.exceptions.ValidationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String body = null;
        try {
            if (response.body() != null) {
                body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            return new RuntimeException("Ошибка при чтении тела ответа: " + e.getMessage(), e);
        }

        String message = "Неизвестная ошибка при вызове " + methodKey + " (статус " + response.status() + ")";
        if (body != null && !body.isEmpty()) {
            try {
                ApiError apiError = objectMapper.readValue(body, ApiError.class);
                message = apiError.getMessage();
            } catch (IOException ignored) {
            }
        }

        return switch (response.status()) {
            case 404 -> new NotFoundException(message);
            case 400 -> new ValidationException(message);
            default -> new RuntimeException(message);
        };
    }
}


