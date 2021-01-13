package com.nospace.model;

import com.nospace.validators.UsernameAlreadyInUse;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class EditUserRequest {

    @NotBlank(message = "Username must not be blank")
    @NotNull
    @Size(min = 3, max = 50, message = "Username must at least 3 characters long")
    @UsernameAlreadyInUse
    private String username;

    @NotNull
    private String password;

}
