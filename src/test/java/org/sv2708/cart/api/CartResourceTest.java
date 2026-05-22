package org.sv2708.cart.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class CartResourceTest {

    @Test
    void createCartRequiresCartId() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "customerId": "customer-1",
                          "items": []
                        }
                        """)
                .when()
                .post("/carts")
                .then()
                .statusCode(400)
                .body("message", is("cartId is required"));
    }
}
