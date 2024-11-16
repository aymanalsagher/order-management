package com.github.aymanalsagher.orderservice.service;

import static org.junit.jupiter.api.Assertions.*;

import com.github.aymanalsagher.orderservice.model.Ordering;
import com.github.aymanalsagher.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.Mockito.*;

class OrderingServiceTest {

    private OrderRepository orderRepository;
    private RestTemplate restTemplate;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        restTemplate = mock(RestTemplate.class);
        orderService = new OrderService(orderRepository, restTemplate);
    }

    @Test
    void placeOrder_ShouldReturnConfirmedOrder_WhenInventoryIsAvailable() {
        Long itemId = 1L;
        int quantity = 2;

        when(restTemplate.postForObject("http://localhost:8081/inventory/" + itemId + "/decrease?quantity=" + quantity, null, Boolean.class))
                .thenReturn(true);

        Ordering mockOrdering = new Ordering();
        mockOrdering.setId(1L);
        mockOrdering.setItemId(itemId);
        mockOrdering.setQuantity(quantity);
        mockOrdering.setStatus("CONFIRMED");

        when(orderRepository.save(Mockito.any(Ordering.class))).thenReturn(mockOrdering);

        Ordering placedOrdering = orderService.placeOrder(itemId, quantity);

        assertNotNull(placedOrdering);
        assertEquals("CONFIRMED", placedOrdering.getStatus());
        verify(orderRepository, times(1)).save(Mockito.any(Ordering.class));
    }

    @Test
    void placeOrder_ShouldReturnFailedOrder_WhenInventoryIsNotAvailable() {
        Long itemId = 1L;
        int quantity = 2;

        when(restTemplate.postForObject("http://localhost:8081/inventory/" + itemId + "/decrease?quantity=" + quantity, null, Boolean.class))
                .thenReturn(false);

        Ordering mockOrdering = new Ordering();
        mockOrdering.setId(1L);
        mockOrdering.setItemId(itemId);
        mockOrdering.setQuantity(quantity);
        mockOrdering.setStatus("FAILED");

        when(orderRepository.save(Mockito.any(Ordering.class))).thenReturn(mockOrdering);

        Ordering placedOrdering = orderService.placeOrder(itemId, quantity);

        assertNotNull(placedOrdering);
        assertEquals("FAILED", placedOrdering.getStatus());
        verify(orderRepository, times(1)).save(Mockito.any(Ordering.class));
    }

    @Test
    void getOrder_ShouldReturnOrder_WhenOrderExists() {
        Long orderId = 1L;

        Ordering mockOrdering = new Ordering();
        mockOrdering.setId(orderId);
        mockOrdering.setItemId(1L);
        mockOrdering.setQuantity(2);
        mockOrdering.setStatus("CONFIRMED");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrdering));

        Optional<Ordering> result = orderService.getOrder(orderId);

        assertTrue(result.isPresent());
        assertEquals(orderId, result.get().getId());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void getOrder_ShouldReturnEmpty_WhenOrderDoesNotExist() {
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Optional<Ordering> result = orderService.getOrder(orderId);

        assertFalse(result.isPresent());
        verify(orderRepository, times(1)).findById(orderId);
    }
}
