package com.ruben.mybank.exception;


public class JWTException extends RuntimeException {

    public JWTException(String msg) {
        super("ERROR: JWTException: " + msg);
    }
}