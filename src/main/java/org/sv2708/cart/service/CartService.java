package org.sv2708.cart.service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.sv2708.cart.api.CreateCartRequest;
import org.sv2708.cart.api.UpdateCartRequest;
import org.sv2708.cart.model.Cart;
import org.sv2708.cart.model.CartItem;
import org.sv2708.cart.repository.CartMapper;
import org.sv2708.cart.repository.CartRepository;

@ApplicationScoped
public class CartService {

    private final CartRepository cartRepository;
    private final Clock clock;

    @Inject
    CartService(CartRepository cartRepository) {
        this(cartRepository, Clock.systemUTC());
    }

    CartService(CartRepository cartRepository, Clock clock) {
        this.cartRepository = cartRepository;
        this.clock = clock;
    }

    public Cart create(CreateCartRequest request) {
        if (request == null) {
            throw new InvalidCartException("request body is required");
        }

        var now = clock.instant();
        var cart = new Cart(
                requireValue(request.cartId(), "cartId"),
                requireValue(request.customerId(), "customerId"),
                "ACTIVE",
                normalizeItems(request.items()),
                CartMapper.totalAmount(request.items()),
                defaultCurrency(request.currency()),
                now,
                now);

        cartRepository.create(cart);
        return cart;
    }

    public List<Cart> list() {
        return cartRepository.list();
    }

    public Optional<Cart> find(String cartId) {
        return cartRepository.find(cartId);
    }

    public Optional<Cart> update(String cartId, UpdateCartRequest request) {
        if (request == null) {
            throw new InvalidCartException("request body is required");
        }

        return find(cartId)
                .map(existing -> {
                    var items = request.items() == null ? existing.items() : normalizeItems(request.items());
                    var updated = new Cart(
                            existing.cartId(),
                            request.customerId() == null ? existing.customerId() : request.customerId(),
                            request.status() == null ? existing.status() : request.status(),
                            items,
                            CartMapper.totalAmount(items),
                            request.currency() == null ? existing.currency() : defaultCurrency(request.currency()),
                            existing.createdAt(),
                            clock.instant());
                    cartRepository.update(updated);
                    return updated;
                });
    }

    public boolean delete(String cartId) {
        return cartRepository.delete(cartId);
    }

    private static String requireValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidCartException(fieldName + " is required");
        }
        return value;
    }

    private static List<CartItem> normalizeItems(List<CartItem> items) {
        if (items == null) {
            return List.of();
        }
        for (CartItem item : items) {
            if (item.productId() == null || item.productId().isBlank()) {
                throw new InvalidCartException("items[].productId is required");
            }
            if (item.name() == null || item.name().isBlank()) {
                throw new InvalidCartException("items[].name is required");
            }
            if (item.quantity() <= 0) {
                throw new InvalidCartException("items[].quantity must be greater than zero");
            }
            if (item.unitPrice() == null || item.unitPrice().signum() < 0) {
                throw new InvalidCartException("items[].unitPrice must be zero or greater");
            }
        }
        return List.copyOf(items);
    }

    private static String defaultCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return "USD";
        }
        return currency;
    }
}
