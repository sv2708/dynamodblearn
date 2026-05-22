package org.sv2708.cart.api;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.sv2708.cart.model.CartItem;

@Schema(description = "Request body for updating a cart. Omitted fields keep their current values.")
public record UpdateCartRequest(
        @Schema(description = "Customer that owns the cart.", examples = "customer-1")
        String customerId,

        @Schema(description = "Cart lifecycle status.", examples = "ACTIVE")
        String status,

        @Schema(description = "Replacement cart items.")
        List<CartItem> items,

        @Schema(description = "ISO 4217 currency code.", examples = "USD")
        String currency) {
}
