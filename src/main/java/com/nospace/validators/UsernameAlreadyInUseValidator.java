package com.nospace.validators;

import com.nospace.entities.User;
import com.nospace.services.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class UsernameAlreadyInUseValidator implements ConstraintValidator<UsernameAlreadyInUse, String> {

    private final UserService userService;
    public UsernameAlreadyInUseValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(UsernameAlreadyInUse constraintAnnotation) {
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        Optional<User> user = userService.findByUsername(username);
        return user.isEmpty();
    }
}
