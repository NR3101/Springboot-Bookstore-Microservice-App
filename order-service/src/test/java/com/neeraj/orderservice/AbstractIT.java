package com.neeraj.orderservice;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Collections.singletonList;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
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
@AutoConfigureMockMvc
public abstract class AbstractIT {

    // Constants for Keycloak testing matching the reference setup
    static final String CLIENT_ID = "bookstore-webapp";
    static final String CLIENT_SECRET = "eSdf5XWCCpET1NBUdJemmanF325Lzp4L";
    static final String USERNAME = "neeraj";
    static final String PASSWORD = "123456";

    @LocalServerPort
    int port;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

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

    /**
     * Obtains an access token from the Keycloak test container to be used in RestAssured tests.
     * This ensures the test requests are authenticated similarly to real requests.
     */
    protected String getToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("grant_type", singletonList("password"));
        map.put("client_id", singletonList(CLIENT_ID));
        map.put("client_secret", singletonList(CLIENT_SECRET));
        map.put("username", singletonList(USERNAME));
        map.put("password", singletonList(PASSWORD));

        String authServerUrl =
                oAuth2ResourceServerProperties.getJwt().getIssuerUri() + "/protocol/openid-connect/token";

        var request = new HttpEntity<>(map, httpHeaders);
        KeyCloakToken token = restTemplate.postForObject(authServerUrl, request, KeyCloakToken.class);

        assert token != null;
        return token.accessToken();
    }

    record KeyCloakToken(@JsonProperty("access_token") String accessToken) {}
}
