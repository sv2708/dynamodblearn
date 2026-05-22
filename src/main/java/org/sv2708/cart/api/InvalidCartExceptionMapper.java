package org.sv2708.cart.api;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.sv2708.cart.service.InvalidCartException;

@Provider
public class InvalidCartExceptionMapper implements ExceptionMapper<InvalidCartException> {

    @Override
    public Response toResponse(InvalidCartException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }
}
