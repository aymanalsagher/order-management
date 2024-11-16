package com.github.aymanalsagher.orderservice.controller;

import com.github.aymanalsagher.orderservice.model.Ordering;
import com.github.aymanalsagher.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ordering placeOrder(@RequestParam String itemId, @RequestParam String quantity) {
        return orderService.placeOrder(Long.parseLong(itemId), Integer.parseInt(quantity));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Ordering> getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
