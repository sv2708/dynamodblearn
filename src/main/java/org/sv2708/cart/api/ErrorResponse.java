package org.sv2708.cart.api;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Standard API error response.")
public record ErrorResponse(String message) {
}
