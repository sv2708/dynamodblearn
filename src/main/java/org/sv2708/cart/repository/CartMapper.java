package org.sv2708.cart.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sv2708.cart.model.Cart;
import org.sv2708.cart.model.CartItem;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public final class CartMapper {

    public static final String CART_ID = "cart_id";
    static final String CUSTOMER_ID = "customer_id";
    static final String STATUS = "status";
    static final String ITEMS = "items";
    static final String PRODUCT_ID = "product_id";
    static final String NAME = "name";
    static final String QUANTITY = "quantity";
    static final String UNIT_PRICE = "unit_price";
    static final String TOTAL_AMOUNT = "total_amount";
    static final String CURRENCY = "currency";
    static final String CREATED_AT = "created_at";
    static final String UPDATED_AT = "updated_at";

    private CartMapper() {
    }

    static Map<String, AttributeValue> toItem(Cart cart) {
        var item = new java.util.HashMap<String, AttributeValue>();
        putString(item, CART_ID, cart.cartId());
        putString(item, CUSTOMER_ID, cart.customerId());
        putString(item, STATUS, cart.status());
        putItems(item, cart.items());
        putNumber(item, TOTAL_AMOUNT, cart.totalAmount());
        putString(item, CURRENCY, cart.currency());
        putInstant(item, CREATED_AT, cart.createdAt());
        putInstant(item, UPDATED_AT, cart.updatedAt());
        return item;
    }

    static Cart fromItem(Map<String, AttributeValue> item) {
        if (item == null || item.isEmpty()) {
            return null;
        }

        return new Cart(
                readString(item, CART_ID),
                readString(item, CUSTOMER_ID),
                readString(item, STATUS),
                readItems(item.get(ITEMS)),
                readBigDecimal(item, TOTAL_AMOUNT),
                readString(item, CURRENCY),
                readInstant(item, CREATED_AT),
                readInstant(item, UPDATED_AT));
    }

    public static BigDecimal totalAmount(List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    static Map<String, AttributeValue> key(String cartId) {
        return Map.of(CART_ID, AttributeValue.builder().s(cartId).build());
    }

    private static void putItems(Map<String, AttributeValue> item, List<CartItem> items) {
        if (items == null) {
            return;
        }

        item.put(ITEMS, AttributeValue.builder()
                .l(items.stream()
                        .map(CartMapper::toItemValue)
                        .toList())
                .build());
    }

    private static AttributeValue toItemValue(CartItem cartItem) {
        return AttributeValue.builder()
                .m(Map.of(
                        PRODUCT_ID, AttributeValue.builder().s(cartItem.productId()).build(),
                        NAME, AttributeValue.builder().s(cartItem.name()).build(),
                        QUANTITY, AttributeValue.builder().n(Integer.toString(cartItem.quantity())).build(),
                        UNIT_PRICE, AttributeValue.builder().n(cartItem.unitPrice().toPlainString()).build()))
                .build();
    }

    private static List<CartItem> readItems(AttributeValue value) {
        if (value == null || value.l() == null) {
            return List.of();
        }

        var cartItems = new ArrayList<CartItem>();
        for (AttributeValue itemValue : value.l()) {
            var item = itemValue.m();
            cartItems.add(new CartItem(
                    readString(item, PRODUCT_ID),
                    readString(item, NAME),
                    Integer.parseInt(item.get(QUANTITY).n()),
                    new BigDecimal(item.get(UNIT_PRICE).n())));
        }
        return cartItems;
    }

    private static void putString(Map<String, AttributeValue> item, String key, String value) {
        if (value != null && !value.isBlank()) {
            item.put(key, AttributeValue.builder().s(value).build());
        }
    }

    private static void putNumber(Map<String, AttributeValue> item, String key, BigDecimal value) {
        if (value != null) {
            item.put(key, AttributeValue.builder().n(value.toPlainString()).build());
        }
    }

    private static void putInstant(Map<String, AttributeValue> item, String key, Instant value) {
        if (value != null) {
            item.put(key, AttributeValue.builder().s(value.toString()).build());
        }
    }

    private static String readString(Map<String, AttributeValue> item, String key) {
        var value = item.get(key);
        return value == null ? null : value.s();
    }

    private static BigDecimal readBigDecimal(Map<String, AttributeValue> item, String key) {
        var value = item.get(key);
        return value == null ? null : new BigDecimal(value.n());
    }

    private static Instant readInstant(Map<String, AttributeValue> item, String key) {
        var value = item.get(key);
        return value == null ? null : Instant.parse(value.s());
    }

}
