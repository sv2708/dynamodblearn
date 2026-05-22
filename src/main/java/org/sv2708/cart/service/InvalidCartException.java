package org.sv2708.cart.service;

public class InvalidCartException extends RuntimeException {

    public InvalidCartException(String message) {
        super(message);
    }
}
