package com.neeraj.orderservice;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.wiremock.integrations.testcontainers.WireMockContainer;

/*
 * @SpringBootTest is used to create an application context for integration tests.It tests the application as a whole including all the layers of the application.
 * @LocalServerPort is used to inject the port of the local server.
 * Wiremock is used to mock the external catalog service. It allows us to define expected requests and responses for the catalog service, which helps in testing the order service without relying on the actual catalog service.
 * @DynamicPropertySource is used to dynamically set the properties for the tests. In this case, we are setting the catalog service URL to the WireMock server's base URL, so that the order service can communicate with the mocked catalog service during the tests.
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

    static WireMockContainer wireMockServer = new WireMockContainer("wiremock/wiremock:3.13.1");

    @BeforeAll
    static void beforeAll() {
        wireMockServer.start();
        configureFor(wireMockServer.getHost(), wireMockServer.getPort());
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("orders.catalog-service-url", wireMockServer::getBaseUrl);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    protected static void mockGetProductByCode(String code, String name, BigDecimal price) {
        stubFor(WireMock.get(urlMatching("/api/products/" + code))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(
                                """
                                            {
                                                "code": "%s",
                                                "name": "%s",
                                                "price": %f
                                            }
                                        """
                                        .formatted(code, name, price.doubleValue()))));
    }
}
