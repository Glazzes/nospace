package com.nospace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class FileNotWrittenException extends RuntimeException{
    public FileNotWrittenException(String message){
        super(message);
    }
}
