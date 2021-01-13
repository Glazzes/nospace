package com.nospace.exception;

import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(FileNotWrittenException.class)
    public ResponseEntity<ExceptionDetails> handleFileNotWrittenException(
        FileNotWrittenException fileNotWrittenException
    ){
        ExceptionDetails details = ExceptionDetails.builder()
            .message(fileNotWrittenException.getMessage())
            .thrownAt(LocalDateTime.now())
            .causedBy(FileNotWrittenException.class.getName())
            .build();

        return new ResponseEntity<ExceptionDetails>(details, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(StorageCapacityExceededException.class)
    public ResponseEntity<ExceptionDetails> handleStorageExceeded(
        StorageCapacityExceededException exception
    ){
        ExceptionDetails details = ExceptionDetails.builder()
            .message(exception.getMessage())
            .thrownAt(LocalDateTime.now())
            .causedBy(FileNotWrittenException.class.getName())
            .build();

        return new ResponseEntity<ExceptionDetails>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FolderNotFoundException.class)
    public ResponseEntity<ExceptionDetails> handleFolderNotFoundException(
        FolderNotFoundException exception
    ){
        ExceptionDetails details = ExceptionDetails.builder()
            .message(exception.getMessage())
            .thrownAt(LocalDateTime.now())
            .causedBy(FolderNotFoundException.class.getName())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(details);
    }

    @ExceptionHandler(RepresentationalFileNotFoundExcepion.class)
    public ResponseEntity<ExceptionDetails> handleRepresentationalFileNotFoundExcepion(
        RepresentationalFileNotFoundExcepion excepion
    ){
        ExceptionDetails details = ExceptionDetails.builder()
            .message(excepion.getMessage())
            .thrownAt(LocalDateTime.now())
            .causedBy(RepresentationalFileNotFoundExcepion.class.getName())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(details);
    }

}
