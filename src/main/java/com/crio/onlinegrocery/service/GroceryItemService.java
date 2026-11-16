package com.crio.onlinegrocery.service;

import com.crio.onlinegrocery.dto.GroceryItemRequestDto;
import com.crio.onlinegrocery.dto.GroceryItemResponseDto;

import java.util.List;

public interface GroceryItemService {
    GroceryItemResponseDto createGroceryItem(GroceryItemRequestDto requestDto);
    List<GroceryItemResponseDto> getAllGroceryItems(String category);
    GroceryItemResponseDto getGroceryItemById(Long id);
    GroceryItemResponseDto updateGroceryItem(Long id, GroceryItemRequestDto requestDto);
    void deleteGroceryItem(Long id);
}
