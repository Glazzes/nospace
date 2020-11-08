package com.nospace.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExceptionDetails {

    private String message;
    private String causedBy;
    private LocalDateTime thrownAt;
}
