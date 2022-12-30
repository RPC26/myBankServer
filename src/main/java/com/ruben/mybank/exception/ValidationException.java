package com.ruben.mybank.exception;


public class ValidationException extends RuntimeException {

    public ValidationException(String msg) {
        super("ERROR: Field data not valid: " + msg);
    }

}
