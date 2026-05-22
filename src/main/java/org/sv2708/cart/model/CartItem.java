package org.sv2708.cart.model;

import java.math.BigDecimal;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Item stored in a shopping cart.")
public record CartItem(
        @Schema(description = "Product identifier.", examples = "product-1")
        String productId,

        @Schema(description = "Product display name.", examples = "Keyboard")
        String name,

        @Schema(description = "Quantity of this product in the cart.", examples = "1", minimum = "1")
        int quantity,

        @Schema(description = "Price for one unit of the product.", examples = "49.99", minimum = "0")
        BigDecimal unitPrice) {
}
