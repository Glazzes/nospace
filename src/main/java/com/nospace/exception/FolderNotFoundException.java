package com.nospace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FolderNotFoundException extends RuntimeException{
    public FolderNotFoundException(String message) {
        super(message);
    }

    public FolderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
