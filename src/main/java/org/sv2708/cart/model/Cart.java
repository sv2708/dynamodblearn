package org.sv2708.cart.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Shopping cart persisted in DynamoDB.")
public record Cart(
        @Schema(description = "Cart identifier and DynamoDB partition key.", examples = "cart-1001")
        String cartId,

        @Schema(description = "Customer that owns the cart.", examples = "customer-1")
        String customerId,

        @Schema(description = "Cart lifecycle status.", examples = "ACTIVE")
        String status,

        @Schema(description = "Products currently in the cart.")
        List<CartItem> items,

        @Schema(description = "Computed cart total.", examples = "49.99")
        BigDecimal totalAmount,

        @Schema(description = "ISO 4217 currency code.", examples = "USD")
        String currency,

        @Schema(description = "Creation timestamp in UTC.")
        Instant createdAt,

        @Schema(description = "Last update timestamp in UTC.")
        Instant updatedAt) {
}
