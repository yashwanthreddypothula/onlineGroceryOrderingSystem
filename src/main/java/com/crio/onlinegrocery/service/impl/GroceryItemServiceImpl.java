package com.crio.onlinegrocery.service.impl;

import com.crio.onlinegrocery.dto.GroceryItemRequestDto;
import com.crio.onlinegrocery.dto.GroceryItemResponseDto;
import com.crio.onlinegrocery.entity.GroceryItem;
import com.crio.onlinegrocery.exception.ResourceNotFoundException;
import com.crio.onlinegrocery.repository.GroceryItemRepository;
import com.crio.onlinegrocery.service.GroceryItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroceryItemServiceImpl implements GroceryItemService {

    private final GroceryItemRepository groceryItemRepository;

    public GroceryItemServiceImpl(GroceryItemRepository groceryItemRepository) {
        this.groceryItemRepository = groceryItemRepository;
    }

    @Override
    public GroceryItemResponseDto createGroceryItem(GroceryItemRequestDto requestDto) {
        GroceryItem item = new GroceryItem();
        item.setName(requestDto.getName());
        item.setCategory(requestDto.getCategory());
        item.setPrice(requestDto.getPrice());
        item.setQuantity(requestDto.getQuantity());

        GroceryItem savedItem = groceryItemRepository.save(item);
        return mapToResponseDto(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroceryItemResponseDto> getAllGroceryItems(String category) {
        List<GroceryItem> items;
        if (category != null && !category.trim().isEmpty()) {
            items = groceryItemRepository.findByCategoryIgnoreCase(category.trim());
        } else {
            items = groceryItemRepository.findAll();
        }

        return items.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GroceryItemResponseDto getGroceryItemById(Long id) {
        GroceryItem item = groceryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grocery item not found with id: " + id));
        return mapToResponseDto(item);
    }

    @Override
    public GroceryItemResponseDto updateGroceryItem(Long id, GroceryItemRequestDto requestDto) {
        GroceryItem item = groceryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grocery item not found with id: " + id));

        item.setName(requestDto.getName());
        item.setCategory(requestDto.getCategory());
        item.setPrice(requestDto.getPrice());
        item.setQuantity(requestDto.getQuantity());

        GroceryItem updatedItem = groceryItemRepository.save(item);
        return mapToResponseDto(updatedItem);
    }

    @Override
    public void deleteGroceryItem(Long id) {
        GroceryItem item = groceryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grocery item not found with id: " + id));
        groceryItemRepository.delete(item);
    }

    private GroceryItemResponseDto mapToResponseDto(GroceryItem item) {
        return new GroceryItemResponseDto(
                item.getId(),
                item.getName(),
                item.getCategory(),
                item.getPrice(),
                item.getQuantity()
        );
    }
}
