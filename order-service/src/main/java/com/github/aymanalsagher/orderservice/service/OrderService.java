package com.github.aymanalsagher.orderservice.service;

import com.github.aymanalsagher.orderservice.model.Ordering;
import com.github.aymanalsagher.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class OrderService {

    @Value("${inventory.endpoint}")
    private String inventoryEndpoint;

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public Optional<Ordering> getOrder(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public Ordering placeOrder(Long itemId, int quantity) {
        Map<String, Integer> body = new HashMap<>();
        body.put("quantity", quantity);
        String url = "http://"+inventoryEndpoint+":8081/v1/inventory/" + itemId + "/decrease";
        boolean available;
        try {
            log.info("Calling [{}]", url);
            restTemplate.postForEntity(url, body, Void.class);
            available = true;
        } catch (Exception ex) {
            available = false;
        }

        Ordering ordering = new Ordering();
        ordering.setItemId(itemId);
        ordering.setQuantity(quantity);
        ordering.setStatus(available ? "AVAILABLE" : "FAILED");

        return orderRepository.save(ordering);
    }
}
