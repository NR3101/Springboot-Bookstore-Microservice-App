package com.neeraj.orderservice.web.controllers;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neeraj.orderservice.domain.OrderService;
import com.neeraj.orderservice.domain.SecurityService;
import com.neeraj.orderservice.domain.models.CreateOrderRequest;
import com.neeraj.orderservice.testdata.TestDataFactory;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/*
 * @WebMvcTest is used to test the web layer of the application. It loads only the specified controller and its related components,rather than a full application context load(@SpringBootTest).
 * @MockitoBean is used to create mock instances of the specified beans and inject them into the application context. This allows you to isolate the controller being tested and mock the behavior of its dependencies.
 * MockMvc is used to perform HTTP requests and assert the responses in a test environment without starting a real server.
 * */

@WebMvcTest(OrderController.class)
class OrderControllerUnitTest {

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private SecurityService securityService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        given(securityService.getLoginUserName()).willReturn("testuser");
    }

    /*
     * @ParameterizedTest is used to run the same test with different inputs. It allows you to define a test method that takes parameters and then provide a set of arguments to run the test with.
     * @MethodSource is used to specify a method that provides the arguments for the parameterized test. The method must return a Stream of Arguments, where each Arguments instance represents a set of parameters for a single test execution.
     * */

    @ParameterizedTest(name = "[{index}]-{0}")
    @MethodSource("createOrderRequestProvider")
    @WithMockUser
    void shouldReturnBadRequest_WhenOrderPayloadIsInvalid(CreateOrderRequest request) throws Exception {
        given(orderService.createOrder(eq("testuser"), any(CreateOrderRequest.class)))
                .willReturn(null);

        mockMvc.perform(post("/api/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    static Stream<Arguments> createOrderRequestProvider() {
        return Stream.of(
                arguments(
                        named("Order with invalid customer", TestDataFactory.createOrderRequestWithInvalidCustomer())),
                arguments(named(
                        "Order with invalid delivery address",
                        TestDataFactory.createOrderRequestWithInvalidDeliveryAddress())),
                arguments(named("Order with no items", TestDataFactory.createOrderRequestWithNoItems())));
    }
}
