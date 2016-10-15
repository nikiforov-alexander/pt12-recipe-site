package com.techdegree.exception;

// TODO : refactor this to be used with @ControllerAdvice and remove try catch
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User with this username already exists");
    }
}
