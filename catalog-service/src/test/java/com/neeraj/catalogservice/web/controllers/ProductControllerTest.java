package com.neeraj.catalogservice.web.controllers;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.neeraj.catalogservice.AbstractIT;
import com.neeraj.catalogservice.domain.Product;
import io.restassured.http.ContentType;
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
        given().contentType(ContentType.JSON)
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

    @Test
    void shouldReturnProductByCode_WhenValidCode() {
        Product product = given().contentType(ContentType.JSON)
                .when()
                .get("/api/products/{code}", "P114")
                .then()
                .statusCode(200)
                .assertThat()
                .extract()
                .body()
                .as(Product.class);

        assertThat(product).isNotNull();
    }

    @Test
    void shouldReturnNotFound_WhenInvalidCode() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/products/{code}", "invalid-code")
                .then()
                .statusCode(404)
                .body("status", is(404))
                .body("title", is("Product Not Found"));
    }
}
