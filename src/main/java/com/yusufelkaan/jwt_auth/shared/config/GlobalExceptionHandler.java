package com.yusufelkaan.jwt_auth.shared.config;

import com.yusufelkaan.jwt_auth.shared.dtos.ApiResponse;
import com.yusufelkaan.jwt_auth.shared.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

// @ControllerAdvice makes this class globally applicable to all @Controllers
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        System.err.println("Resource Not Found Error: " + ex.getMessage());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handles 400 Bad Request errors resulting from DTO validation failure (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        // Create a map to hold all field errors and their messages
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Build the response, placing the field errors in the 'data' payload
        ApiResponse<Map<String, String>> errorResponse = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed. Please check the fields in the 'data' object for details.")
                .data(errors)
                .build();

        // Return 400 BAD_REQUEST status
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handles 500 Internal Server Errors (Generic Catch-All)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {

        // Log the stack trace for critical server-side debugging
        ex.printStackTrace();

        // Build the standardized error response
        ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
                .success(false)
                // Do NOT expose internal technical details to the client
                .message("An unexpected internal error occurred. Please contact support.")
                .data(null)
                .build();

        // Return 500 INTERNAL_SERVER_ERROR status
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
