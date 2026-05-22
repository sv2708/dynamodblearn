package org.sv2708.cart.api;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.sv2708.cart.model.CartItem;

@Schema(description = "Request body for creating a cart.")
public record CreateCartRequest(
        @Schema(description = "Cart identifier. This becomes the DynamoDB cart_id key.", required = true, examples = "cart-1001")
        String cartId,

        @Schema(description = "Customer that owns the cart.", required = true, examples = "customer-1")
        String customerId,

        @Schema(description = "Initial cart items.")
        List<CartItem> items,

        @Schema(description = "ISO 4217 currency code. Defaults to USD when omitted.", examples = "USD")
        String currency) {
}
