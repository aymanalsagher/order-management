package com.github.aymanalsagher.inventoryservice.service;

import com.github.aymanalsagher.inventoryservice.model.Item;
import com.github.aymanalsagher.inventoryservice.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {

    private final ItemRepository itemRepository;

    public InventoryService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Optional<Item> getItem(Long id) {
        return itemRepository.findById(id);
    }

    public boolean decreaseStock(Long itemId, Integer quantity) {
        return itemRepository.findById(itemId).map(item ->{
            if (quantity != null && quantity <= item.getQuantity()) {
                item.setQuantity(item.getQuantity() - quantity);
                itemRepository.save(item);
                return true;
            } else {
                return false;
            }
        }).orElse(false);
    }

}
