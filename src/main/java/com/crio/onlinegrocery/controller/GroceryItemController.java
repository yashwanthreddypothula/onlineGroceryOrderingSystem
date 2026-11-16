package com.crio.onlinegrocery.controller;

import com.crio.onlinegrocery.dto.GroceryItemRequestDto;
import com.crio.onlinegrocery.dto.GroceryItemResponseDto;
import com.crio.onlinegrocery.service.GroceryItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/grocery-items")
@Tag(name = "Grocery Item Management", description = "CRUD endpoints for managing grocery inventory items")
public class GroceryItemController {

    private final GroceryItemService groceryItemService;

    public GroceryItemController(GroceryItemService groceryItemService) {
        this.groceryItemService = groceryItemService;
    }

    @PostMapping
    @Operation(summary = "Create a new grocery item", description = "Adds a new grocery item with name, category, price, and stock quantity")
    public ResponseEntity<GroceryItemResponseDto> createGroceryItem(@Valid @RequestBody GroceryItemRequestDto requestDto) {
        GroceryItemResponseDto createdItem = groceryItemService.createGroceryItem(requestDto);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all grocery items", description = "Retrieves all grocery items, optionally filtered by category")
    public ResponseEntity<List<GroceryItemResponseDto>> getAllGroceryItems(
            @Parameter(description = "Optional category filter (e.g., Dairy, Produce, Bakery)")
            @RequestParam(required = false) String category) {
        List<GroceryItemResponseDto> items = groceryItemService.getAllGroceryItems(category);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get grocery item by ID", description = "Retrieves details of a specific grocery item by its ID")
    public ResponseEntity<GroceryItemResponseDto> getGroceryItemById(@PathVariable Long id) {
        GroceryItemResponseDto item = groceryItemService.getGroceryItemById(id);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update grocery item details", description = "Updates details of an existing grocery item")
    public ResponseEntity<GroceryItemResponseDto> updateGroceryItem(
            @PathVariable Long id,
            @Valid @RequestBody GroceryItemRequestDto requestDto) {
        GroceryItemResponseDto updatedItem = groceryItemService.updateGroceryItem(id, requestDto);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete grocery item", description = "Removes a grocery item from inventory by its ID")
    public ResponseEntity<Void> deleteGroceryItem(@PathVariable Long id) {
        groceryItemService.deleteGroceryItem(id);
        return ResponseEntity.noContent().build();
    }
}
