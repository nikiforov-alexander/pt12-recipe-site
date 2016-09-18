package com.techdegree.validator;

import com.techdegree.model.Item;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

// This class is used to validate if Item.id is not 0
// because when it is zero, Recipe.Ingredient.Item is
// not selected by user. It is not necessary function
// but it is good to know the way it works
public class ItemValidator
        implements ConstraintValidator<ValidItem, Item> {
    @Override
    public void initialize(ValidItem constraintAnnotation) {
        // don't know what to write here .. ?
        // may be Hibernate.initialize or stuff ?
    }

    @Override
    public boolean isValid(Item value, ConstraintValidatorContext context) {
        if (value.getId() == 0) {
            return false;
        }
        return true;
    }
}
