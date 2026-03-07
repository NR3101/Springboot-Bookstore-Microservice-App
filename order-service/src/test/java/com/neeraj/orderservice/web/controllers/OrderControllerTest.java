package com.neeraj.orderservice.web.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.neeraj.orderservice.AbstractIT;
import com.neeraj.orderservice.domain.models.OrderSummary;
import com.neeraj.orderservice.testdata.TestDataFactory;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

/*
 * @Nested is used to group related test cases together. It helps in organizing the test cases and makes it easier to read and maintain.
 * */

@Sql("/test-orders.sql")
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

    @Nested
    class GetOrdersTest {

        @Test
        void shouldGetOrdersSuccessfully() {
            List<OrderSummary> orders = given().when()
                    .get("/api/orders")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .body()
                    .as(new TypeRef<>() {});

            // Assert that we got a list of orders
            assertThat(orders).hasSize(2);
        }
    }

    @Nested
    class GetOrderByOrderNumberTest {
        String orderNumber = "order-123";

        @Test
        void shouldGetOrderByOrderNumberSuccessfully() {
            given().when()
                    .get("/api/orders/{orderNumber}", orderNumber)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("orderNumber", is(orderNumber))
                    .body("items.size()", is(2));
        }

        @Test
        void shouldReturnNotFoundForNonExistingOrder() {
            given().when()
                    .get("/api/orders/{orderNumber}", "NON_EXISTING_ORDER")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }
}
