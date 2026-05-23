package org.sv2708.cart.api;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.sv2708.cart.model.Cart;
import org.sv2708.cart.service.CartService;

@Path("/carts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Cart", description = "CRUD operations for shopping carts stored in DynamoDB.")
public class CartResource {

    private final CartService cartService;

    CartResource(CartService cartService) {
        this.cartService = cartService;
    }

    @POST
    @Operation(summary = "Create a cart", description = "Creates a cart in DynamoDB using cartId as the cart_id partition key.")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Cart created.",
                    content = @Content(schema = @Schema(implementation = Cart.class))),
            @APIResponse(responseCode = "400", description = "Invalid create request.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "409", description = "Cart already exists.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response create(CreateCartRequest request) {
        var cart = cartService.create(request);
        return Response.status(Response.Status.CREATED)
                .entity(cart)
                .build();
    }

    @GET
    @Operation(summary = "List carts", description = "Returns all carts from the configured DynamoDB table.")
    @APIResponse(responseCode = "200", description = "Carts returned.",
            content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = Cart.class)))
    public Response list() {
        return Response.ok(cartService.list()).build();
    }

    @GET
    @Path("/{cartId}")
    @Operation(summary = "Get a cart", description = "Returns a cart by cartId.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Cart returned.",
                    content = @Content(schema = @Schema(implementation = Cart.class))),
            @APIResponse(responseCode = "404", description = "Cart not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Cart get(
            @Parameter(description = "Cart identifier.", required = true, example = "cart-1001")
            @PathParam("cartId") String cartId) {
        return cartService.find(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found: " + cartId));
    }

    @PATCH
    @Path("/{cartId}")
    @Operation(summary = "Patch a cart", description = "Partially updates mutable cart fields without a prior read. Omitted fields keep their existing values.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Cart updated.",
                    content = @Content(schema = @Schema(implementation = Cart.class))),
            @APIResponse(responseCode = "400", description = "Invalid update request.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "404", description = "Cart not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Cart patch(
            @Parameter(description = "Cart identifier.", required = true, example = "cart-1001")
            @PathParam("cartId") String cartId,
            UpdateCartRequest request) {
        return cartService.patch(cartId, request)
                .orElseThrow(() -> new NotFoundException("Cart not found: " + cartId));
    }

    @DELETE
    @Path("/{cartId}")
    @Operation(summary = "Delete a cart", description = "Deletes a cart by cartId.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Cart deleted."),
            @APIResponse(responseCode = "404", description = "Cart not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response delete(
            @Parameter(description = "Cart identifier.", required = true, example = "cart-1001")
            @PathParam("cartId") String cartId) {
        if (!cartService.delete(cartId)) {
            throw new NotFoundException("Cart not found: " + cartId);
        }
        return Response.noContent().build();
    }

}
