package org.example.casino.api;

import org.example.casino.model.APIErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIErrors> handleRuntimeException(RuntimeException ex){
        APIErrors apiErrors = APIErrors.builder()
                .withMessage(ex.getMessage())
                .build();
        return new ResponseEntity<>(apiErrors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIErrors> handleRuntimeException(IllegalArgumentException ex){
        APIErrors apiErrors = APIErrors.builder()
                .withMessage(ex.getMessage())
                .build();
        return new ResponseEntity<>(apiErrors, HttpStatus.NOT_FOUND);
    }
}