package com.neeraj.orderservice.web.controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import com.neeraj.orderservice.AbstractIT;
import com.neeraj.orderservice.testdata.TestDataFactory;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/*
 * @Nested is used to group related test cases together. It helps in organizing the test cases and makes it easier to read and maintain.
 * */

class OrderControllerTest extends AbstractIT {

    @Nested
    class CreateOrderTest {

        @Test
        void shouldCreateOrderSuccessfully() {
            mockGetProductByCode("P100", "Product A", BigDecimal.valueOf(34));
            mockGetProductByCode("P104", "Product B", BigDecimal.valueOf(14.5));

            var payload =
                    """
                                {
                                    "customer": {
                                        "name": "John Doe",
                                        "email": "john.doe@example.com",
                                        "phone": "1234567890"
                                    },
                                    "deliveryAddress": {
                                        "addressLine1": "123 Main St",
                                        "addressLine2": "Apt 4B",
                                        "city": "Anytown",
                                        "state": "CA",
                                        "zipCode": "12345",
                                        "country": "USA"
                                    },
                                    "items": [
                                        {
                                            "code": "P100",
                                            "name": "Product A",
                                            "quantity": 2,
                                            "price": 34
                                        },
                                        {
                                            "code": "P104",
                                            "name": "Product B",
                                            "quantity": 1,
                                            "price": 14.5
                                        }
                                    ]
                                }
                            """;

            given().contentType(ContentType.JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("orderNumber", notNullValue());
        }

        @Test
        void shouldReturnBadRequestWhenMandatoryDataIsMissing() {
            var payload = TestDataFactory.createOrderRequestWithInvalidCustomer();
            given().contentType(ContentType.JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }
}
