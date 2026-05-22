package org.sv2708.cart.service;

public class CartAlreadyExistsException extends RuntimeException {

    public CartAlreadyExistsException(String cartId, Throwable cause) {
        super("Cart already exists: " + cartId, cause);
    }
}
