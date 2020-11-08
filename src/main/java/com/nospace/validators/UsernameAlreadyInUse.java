package com.nospace.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UsernameAlreadyInUseValidator.class})
public @interface UsernameAlreadyInUse {
    String message() default "There is an account registered with this username";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
