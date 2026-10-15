
package com.orderplatform.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationErrors(
            MethodArgumentNotValidException exception) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        fieldErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", 400);
        response.put("error", "Validation failed");
        response.put("fieldErrors", fieldErrors);

        return response;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>>
            handleResponseStatusException(
                    ResponseStatusException exception) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", exception.getStatusCode().value());
        response.put("error", exception.getReason());

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(response);
    }
}
