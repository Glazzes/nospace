package com.nospace.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class StorageCapacityExceededException extends RuntimeException{
    public StorageCapacityExceededException(String message){
        super(message);
    }
}
