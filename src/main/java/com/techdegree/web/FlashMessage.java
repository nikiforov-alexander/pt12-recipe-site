package com.techdegree.web;

// class used as flash message
// with message and enum Status
public class FlashMessage {

    // fields

    private String message;
    private Status status;

    // getters and setters :
    // because enum is inner class
    // here

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // constructors

    public FlashMessage(String message, Status status) {
        this.message = message;
        this.status = status;
    }

    // inner enum Status
    public enum Status {
        SUCCESS,
        INFO,
        FAILURE
    }
}
