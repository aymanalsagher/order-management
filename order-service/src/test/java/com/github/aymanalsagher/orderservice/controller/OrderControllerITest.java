package com.github.aymanalsagher.orderservice.controller;

import com.github.aymanalsagher.orderservice.model.Ordering;
import com.github.aymanalsagher.orderservice.repository.OrderRepository;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerITest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate realRestTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        mockServer = MockRestServiceServer.createServer(realRestTemplate);
    }

    @Test
    void createOrder_ShouldReturnAVAILABLEOrder_WhenInventoryAvailable() {
        long itemId = 1L;
        int quantity = 5;

        mockServer.expect(requestTo("http://localhost:8081/v1/inventory/" + itemId + "/decrease"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"success\": true}", APPLICATION_JSON));

        ResponseEntity<Ordering> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/orders?itemId=" + itemId + "&quantity=" + quantity,
                null,
                Ordering.class
        );

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("AVAILABLE", response.getBody().getStatus());
    }

    @Test
    void createOrder_ShouldReturnFailedOrder_WhenInventoryNotAvailable() {
        long itemId = 1L;
        int quantity = 1000;


        mockServer.expect(requestTo("http://localhost:8081/v1/inventory/" + itemId + "/decrease"))
                .andExpect(content().string("{\"quantity\":1000}"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest());

        ResponseEntity<Ordering> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/orders?itemId=" + itemId + "&quantity=" + quantity,
                null,
                Ordering.class
        );

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("FAILED", response.getBody().getStatus());
    }

    @Test
    void retrieveOrder_ShouldReturnOrderDetails_WhenOrderExists() {
        Ordering mockOrder = new Ordering();
        mockOrder.setItemId(1L);
        mockOrder.setQuantity(5);
        mockOrder.setStatus("AVAILABLE");
        Ordering savedOrder = orderRepository.save(mockOrder);

        ResponseEntity<Ordering> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/orders/" + savedOrder.getId(),
                Ordering.class
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(savedOrder.getId(), response.getBody().getId());
        assertEquals("AVAILABLE", response.getBody().getStatus());
    }

    @Test
    void retrieveOrder_ShouldReturnNotFound_WhenOrderDoesNotExist() {
        long nonExistentOrderId = 999L;

        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/v1/orders/" + nonExistentOrderId,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Void.class
        );

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void listOrders_ShouldReturnOrdersById() {
        Ordering order1 = new Ordering();
        order1.setItemId(1L);
        order1.setQuantity(5);
        order1.setStatus("AVAILABLE");

        Ordering order2 = new Ordering();
        order2.setItemId(2L);
        order2.setQuantity(10);
        order2.setStatus("FAILED");

        orderRepository.saveAll(List.of(order1, order2));

        // Act: Call the API endpoint
        ResponseEntity<Ordering> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/orders/" + order1.getId(), Ordering.class);

        // Assert: Verify the response
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(order1.getId(), response.getBody().getId());
        assertEquals("AVAILABLE", response.getBody().getStatus());

        // Act: Call the API endpoint
        ResponseEntity<Ordering> response2 = restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/orders/" + order2.getId(), Ordering.class);

        // Assert: Verify the response
        assertEquals(200, response2.getStatusCode().value());
        assertNotNull(response2.getBody());
        assertEquals(order2.getId(), response2.getBody().getId());
        assertEquals("FAILED", response2.getBody().getStatus());
    }
}
