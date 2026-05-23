package org.sv2708.cart.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.sv2708.cart.api.UpdateCartRequest;
import org.sv2708.cart.model.Cart;

public interface CartRepository {

    void create(Cart cart);

    List<Cart> list();

    Optional<Cart> find(String cartId);

    Optional<Cart> patch(String cartId, UpdateCartRequest request, Instant updatedAt);

    boolean delete(String cartId);
}
