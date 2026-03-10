package com.neeraj.orderservice;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    }

    @Bean
    @ServiceConnection
    RabbitMQContainer rabbitContainer() {
        return new RabbitMQContainer(DockerImageName.parse("rabbitmq:latest"));
    }

    // This creates a Keycloak container for testing so that it behaves like the real authorization server.
    // It dynamically loads the exported bookstore-realm.json
    @Bean
    KeycloakContainer keycloak() {
        return new KeycloakContainer("quay.io/keycloak/keycloak:26.3.0").withRealmImportFile("/bookstore-realm.json");
    }

    // DynamicPropertyRegistrar is used to bind properties from Testcontainers.
    // Here we tell Spring Security where to find the issuer URI of the Keycloak test instance.
    @Bean
    DynamicPropertyRegistrar dynamicPropertyRegistrar(KeycloakContainer keycloak) {
        return (registry) -> {
            registry.add(
                    "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                    () -> keycloak.getAuthServerUrl() + "/realms/bookstore");
        };
    }
}
