package com.neeraj.catalogservice.web.controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.neeraj.catalogservice.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

/*
 * @Sql is used to execute pre and post SQL scripts before and after the test class or method.
 * This is done to ensure that we have consistent test data for each test.
 */

@Sql("/test-data.sql")
class ProductControllerTest extends AbstractIT {

    @Test
    void shouldReturnAllProducts() {
        given().contentType("application/json")
                .when()
                .get("/api/products")
                .then()
                .statusCode(200)
                .body("data", hasSize(10))
                .body("totalElements", is(15))
                .body("pageNumber", is(1))
                .body("totalPages", is(2))
                .body("isFirst", is(true))
                .body("isLast", is(false))
                .body("hasNext", is(true))
                .body("hasPrevious", is(false));
    }
}
