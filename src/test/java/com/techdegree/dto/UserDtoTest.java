package com.techdegree.dto;

import org.junit.Test;

import javax.validation.constraints.Pattern;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class UserDtoTest {

    // the way this function works is taken from
    // this nice post:
    // http://stackoverflow.com/questions/29069956/how-to-test-validation-annotations-of-a-class-using-junit
    // there are not so many tests, but because I took that
    // from web, I believe, that it was also tested by
    // someone
    public void passwordRegex(
            String password,
            boolean validates) throws NoSuchFieldException {

        Field field = UserDto.class.getDeclaredField("password");
        Pattern[] annotations = field.getAnnotationsByType(
                Pattern.class
        );
        assertEquals(
                password.matches(
                        annotations[0].regexp()
                ),
                validates
        );
    }

    @Test
    public void emptyPasswordFail() throws Exception {
        passwordRegex("", false);
    }
    @Test
    public void notEnoughSymbolsFail() throws Exception {
        passwordRegex("123abc", false);
    }
    @Test
    public void justNumbersFail() throws Exception {
        passwordRegex("12345678", false);
    }
    @Test
    public void justLettersFail() throws Exception {
        passwordRegex("abcdefgh", false);
    }
    @Test
    public void lettersAndNumbersWithoutCapitalFail() throws Exception {
        passwordRegex("abcdef123", false);
    }

    @Test
    public void passwordWithEightLettersOneCapOneNumberWorks() throws Exception {
        passwordRegex("Qwertyz1", true);
    }
}