package com.nospace.validators;

import com.nospace.entities.User;
import com.nospace.services.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class EmailAlreadyInUseValidator implements ConstraintValidator<EmailAlreadyInUse, String> {

    private final UserService userService;
    public EmailAlreadyInUseValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(EmailAlreadyInUse constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        Optional<User> user = userService.findByEmail(email);
        return user.isEmpty();
    }
}
