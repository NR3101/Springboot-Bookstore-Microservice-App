package com.neeraj.catalogservice.domain;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

/*
 * @DataJpaTest is used to create an slice of the application context for testing JPA repositories.Mtlb agr hame sirf application k ek part ko test krna h isolation m jaise(controllers, repositories, services, etc.) to @SpringBootTest ki jgh @DataJpaTest use kr skte h.
 * By default, @DataJpaTest creates an in-memory database(h2) for testing.But since we are using Testcontainers, we need to specify the database URL.
 */

@DataJpaTest(
        properties = {"spring.test.database.replace=NONE", "spring.datasource.url=jdbc:tc:postgresql:latest:///catalog"
        })
// @Import(TestcontainersConfiguration.class)
@Sql("/test-data.sql")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldReturnAllProducts() {
        assertThat(productRepository.findAll()).hasSize(15);
    }

    @Test
    void shouldReturnProductByCode_WhenValidCode() {
        assertThat(productRepository.findByCode("P114").isPresent()).isTrue();
    }

    @Test
    void shouldReturnEmpty_WhenInvalidCode() {
        assertThat(productRepository.findByCode("invalid-code").isEmpty()).isTrue();
    }
}
