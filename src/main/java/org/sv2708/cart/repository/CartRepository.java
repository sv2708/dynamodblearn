package org.sv2708.cart.repository;

import java.util.List;
import java.util.Optional;

import org.sv2708.cart.model.Cart;

public interface CartRepository {

    void create(Cart cart);

    List<Cart> list();

    Optional<Cart> find(String cartId);

    void update(Cart cart);

    boolean delete(String cartId);
}
