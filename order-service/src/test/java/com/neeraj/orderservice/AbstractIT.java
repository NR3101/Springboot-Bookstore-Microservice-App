package com.neeraj.orderservice;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/*
 * @SpringBootTest is used to create an application context for integration tests.It tests the application as a whole including all the layers of the application.
 * @LocalServerPort is used to inject the port of the local server.
 */

/*
 * This abstract class is used as a base class for all integration tests.
 * It sets up the RestAssured port for all tests.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public abstract class AbstractIT {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }
}
