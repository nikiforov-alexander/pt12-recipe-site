package com.techdegree.validator;

import com.techdegree.dto.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

// checks whether two password matches, used on
// UserDto object
public class PasswordMatchesValidator
    implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        UserDto userDto = (UserDto) value;
        // null check because otherwise get
        // NullPointerException Below
        if (userDto.getPassword() == null) {
            return false;
        } else {
            return userDto.getPassword().equals(
                    userDto.getMatchingPassword()
            );
        }
    }
}
