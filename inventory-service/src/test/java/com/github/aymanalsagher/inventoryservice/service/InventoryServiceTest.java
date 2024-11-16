package com.github.aymanalsagher.inventoryservice.service;

import com.github.aymanalsagher.inventoryservice.model.Item;
import com.github.aymanalsagher.inventoryservice.repository.ItemRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

     private final ItemRepository repository = mock(ItemRepository.class);
     private final InventoryService service = new InventoryService(repository);

     @Test
     void getItem_ShouldReturnItem_WhenItemExists() {
         Item mockItem = new Item();
         mockItem.setId(1L);
         mockItem.setName("Laptop");
         mockItem.setQuantity(10);

         when(repository.findById(1L)).thenReturn(Optional.of(mockItem));

         Optional<Item> result = service.getItem(1L);
         assertTrue(result.isPresent());
         assertEquals("Laptop", result.get().getName());
     }

     @Test
     void getItem_ShouldReturnEmpty_WhenItemDoesNotExist() {
         when(repository.findById(1L)).thenReturn(Optional.empty());

         Optional<Item> result = service.getItem(1L);
         assertFalse(result.isPresent());
     }

     @Test
     void decreaseStock_ShouldReturnTrue_WhenStockIsSufficient() {
         Item mockItem = new Item();
         mockItem.setId(1L);
         mockItem.setName("Laptop");
         mockItem.setQuantity(10);

         when(repository.findById(1L)).thenReturn(Optional.of(mockItem));

         boolean result = service.decreaseStock(1L, 5);
         assertTrue(result);
         verify(repository, times(1)).save(mockItem);
         assertEquals(5, mockItem.getQuantity());
     }

     @Test
     void decreaseStock_ShouldReturnFalse_WhenStockIsInsufficient() {
         Item mockItem = new Item();
         mockItem.setId(1L);
         mockItem.setName("Laptop");
         mockItem.setQuantity(3);

         when(repository.findById(1L)).thenReturn(Optional.of(mockItem));

         boolean result = service.decreaseStock(1L, 5);
         assertFalse(result);
         verify(repository, never()).save(mockItem);
     }

     @Test
     void decreaseStock_ShouldReturnFalse_WhenItemDoesNotExist() {
         when(repository.findById(1L)).thenReturn(Optional.empty());

         boolean result = service.decreaseStock(1L, 5);
         assertFalse(result);
         verify(repository, never()).save(any());
     }


}
