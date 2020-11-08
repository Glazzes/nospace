package com.nospace.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(InvalidVerificationToken.class)
    public ResponseEntity<ExceptionDetails> handleInvalidVerificationException(
            InvalidVerificationToken invalidVerificationToken
    ){
        ExceptionDetails details = ExceptionDetails.builder()
                .message(invalidVerificationToken.getMessage())
                .causedBy(invalidVerificationToken.getClass().getName())
                .thrownAt(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(details);
    }

}
