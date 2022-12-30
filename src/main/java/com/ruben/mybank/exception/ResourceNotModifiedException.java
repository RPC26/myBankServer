package com.ruben.mybank.exception;

public class ResourceNotModifiedException extends RuntimeException {

    public ResourceNotModifiedException(String msg) {
        super("ERROR: Resource not modified: " + msg);
    }

}
