package org.sv2708.cart.api;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.sv2708.cart.service.CartAlreadyExistsException;

@Provider
public class CartAlreadyExistsExceptionMapper implements ExceptionMapper<CartAlreadyExistsException> {

    @Override
    public Response toResponse(CartAlreadyExistsException exception) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }
}
