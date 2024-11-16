package com.github.aymanalsagher.inventoryservice.controller;

import com.github.aymanalsagher.inventoryservice.model.Item;
import com.github.aymanalsagher.inventoryservice.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles({"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryControllerITest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ItemRepository repository;

    @Test
    void getItem_ShouldReturnItem_WhenItemExists() {
        Item item = new Item();
        item.setName("Laptop");
        item.setQuantity(10);
        repository.save(item);

        ResponseEntity<Item> response = restTemplate.getForEntity("http://localhost:" + port + "/v1/inventory/" + item.getId(), Item.class);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Laptop", response.getBody().getName());
    }

    @Test
    void getItem_ShouldReturn404_WhenItemDoesNotExist() {
        ResponseEntity<Item> response = restTemplate.getForEntity("http://localhost:" + port + "/v1/inventory/999", Item.class);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void decreaseStock_ShouldReturn200_WhenStockIsSufficient() {
        Item item = new Item();
        item.setName("Laptop");
        item.setQuantity(10);
        repository.save(item);

        HttpEntity<Map<String, Integer>> httpEntity = new HttpEntity<>(Map.of("quantity", 5));

        ResponseEntity<Void> response = restTemplate.postForEntity("http://localhost:" + port + "/v1/inventory/" + item.getId() + "/decrease", httpEntity, Void.class);
        System.out.println(response);
        assertEquals(200, response.getStatusCodeValue());
        Item updatedItem = repository.findById(item.getId()).orElseThrow();
        assertEquals(5, updatedItem.getQuantity());
    }

    @Test
    void decreaseStock_ShouldReturn400_WhenStockIsInsufficient() {
        Item item = new Item();
        item.setName("Laptop");
        item.setQuantity(3);
        repository.save(item);

        HttpEntity<Map<String, Integer>> httpEntity = new HttpEntity<>(Map.of("quantity", 5));

        ResponseEntity<Void> response = restTemplate.postForEntity("http://localhost:" + port + "/v1/inventory/" + item.getId() + "/decrease", httpEntity, Void.class);

        assertEquals(400, response.getStatusCodeValue());
        Item unchangedItem = repository.findById(item.getId()).orElseThrow();
        assertEquals(3, unchangedItem.getQuantity());
    }
}