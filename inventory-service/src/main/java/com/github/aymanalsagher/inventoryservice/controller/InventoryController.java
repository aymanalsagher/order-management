package com.github.aymanalsagher.inventoryservice.controller;

import com.github.aymanalsagher.inventoryservice.model.Item;
import com.github.aymanalsagher.inventoryservice.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItem(@PathVariable Long itemId) {
        return inventoryService.getItem(itemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{itemId}/decrease")
    public ResponseEntity<Void> decreaseItem(@PathVariable Long itemId, @RequestBody Map<String, Integer> body) {
        boolean success = inventoryService.decreaseStock(itemId, body.get("quantity"));
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

}
