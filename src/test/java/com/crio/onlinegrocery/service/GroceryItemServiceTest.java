package com.crio.onlinegrocery.service;

import com.crio.onlinegrocery.dto.GroceryItemRequestDto;
import com.crio.onlinegrocery.dto.GroceryItemResponseDto;
import com.crio.onlinegrocery.entity.GroceryItem;
import com.crio.onlinegrocery.exception.ResourceNotFoundException;
import com.crio.onlinegrocery.repository.GroceryItemRepository;
import com.crio.onlinegrocery.service.impl.GroceryItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroceryItemServiceTest {

    @Mock
    private GroceryItemRepository groceryItemRepository;

    @InjectMocks
    private GroceryItemServiceImpl groceryItemService;

    private GroceryItem sampleItem;
    private GroceryItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        sampleItem = new GroceryItem(1L, "Organic Milk", "Dairy", 3.99, 20);
        requestDto = new GroceryItemRequestDto("Organic Milk", "Dairy", 3.99, 20);
    }

    @Test
    void createGroceryItem_Success() {
        when(groceryItemRepository.save(any(GroceryItem.class))).thenReturn(sampleItem);

        GroceryItemResponseDto response = groceryItemService.createGroceryItem(requestDto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Organic Milk", response.getName());
        assertEquals("Dairy", response.getCategory());
        assertEquals(3.99, response.getPrice());
        verify(groceryItemRepository, times(1)).save(any(GroceryItem.class));
    }

    @Test
    void getAllGroceryItems_NoCategoryFilter() {
        when(groceryItemRepository.findAll()).thenReturn(Arrays.asList(sampleItem));

        List<GroceryItemResponseDto> items = groceryItemService.getAllGroceryItems(null);

        assertEquals(1, items.size());
        assertEquals("Organic Milk", items.get(0).getName());
    }

    @Test
    void getAllGroceryItems_WithCategoryFilter() {
        when(groceryItemRepository.findByCategoryIgnoreCase("Dairy")).thenReturn(Arrays.asList(sampleItem));

        List<GroceryItemResponseDto> items = groceryItemService.getAllGroceryItems("Dairy");

        assertEquals(1, items.size());
        assertEquals("Dairy", items.get(0).getCategory());
    }

    @Test
    void getGroceryItemById_Success() {
        when(groceryItemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

        GroceryItemResponseDto response = groceryItemService.getGroceryItemById(1L);

        assertNotNull(response);
        assertEquals("Organic Milk", response.getName());
    }

    @Test
    void getGroceryItemById_NotFound_ThrowsException() {
        when(groceryItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groceryItemService.getGroceryItemById(99L));
    }

    @Test
    void updateGroceryItem_Success() {
        when(groceryItemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(groceryItemRepository.save(any(GroceryItem.class))).thenReturn(sampleItem);

        GroceryItemResponseDto response = groceryItemService.updateGroceryItem(1L, requestDto);

        assertNotNull(response);
        assertEquals("Organic Milk", response.getName());
        verify(groceryItemRepository, times(1)).save(any(GroceryItem.class));
    }

    @Test
    void deleteGroceryItem_Success() {
        when(groceryItemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        doNothing().when(groceryItemRepository).delete(sampleItem);

        assertDoesNotThrow(() -> groceryItemService.deleteGroceryItem(1L));
        verify(groceryItemRepository, times(1)).delete(sampleItem);
    }
}
