package com.techdegree.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ItemValidator.class)
@Target( {ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidItem {
    String message() default "{Custom Item Validator Error}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
