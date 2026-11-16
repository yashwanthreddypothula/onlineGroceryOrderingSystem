package com.crio.onlinegrocery.controller;

import com.crio.onlinegrocery.dto.GroceryItemRequestDto;
import com.crio.onlinegrocery.dto.GroceryItemResponseDto;
import com.crio.onlinegrocery.exception.ResourceNotFoundException;
import com.crio.onlinegrocery.service.GroceryItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroceryItemController.class)
class GroceryItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroceryItemService groceryItemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createGroceryItem_ValidRequest_Returns201() throws Exception {
        GroceryItemRequestDto requestDto = new GroceryItemRequestDto("Fresh Milk", "Dairy", 3.49, 50);
        GroceryItemResponseDto responseDto = new GroceryItemResponseDto(1L, "Fresh Milk", "Dairy", 3.49, 50);

        when(groceryItemService.createGroceryItem(any(GroceryItemRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/grocery-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Fresh Milk"))
                .andExpect(jsonPath("$.price").value(3.49));
    }

    @Test
    void createGroceryItem_InvalidPrice_Returns400() throws Exception {
        GroceryItemRequestDto requestDto = new GroceryItemRequestDto("Fresh Milk", "Dairy", 0.0, 50);

        mockMvc.perform(post("/api/grocery-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.price").exists());
    }

    @Test
    void getAllGroceryItems_Returns200() throws Exception {
        GroceryItemResponseDto responseDto = new GroceryItemResponseDto(1L, "Fresh Milk", "Dairy", 3.49, 50);
        when(groceryItemService.getAllGroceryItems(null)).thenReturn(Arrays.asList(responseDto));

        mockMvc.perform(get("/api/grocery-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fresh Milk"));
    }

    @Test
    void getGroceryItemById_Found_Returns200() throws Exception {
        GroceryItemResponseDto responseDto = new GroceryItemResponseDto(1L, "Fresh Milk", "Dairy", 3.49, 50);
        when(groceryItemService.getGroceryItemById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/grocery-items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Fresh Milk"));
    }

    @Test
    void getGroceryItemById_NotFound_Returns404() throws Exception {
        when(groceryItemService.getGroceryItemById(99L)).thenThrow(new ResourceNotFoundException("Grocery item not found with id: 99"));

        mockMvc.perform(get("/api/grocery-items/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Grocery item not found with id: 99"));
    }

    @Test
    void updateGroceryItem_Success_Returns200() throws Exception {
        GroceryItemRequestDto requestDto = new GroceryItemRequestDto("Updated Milk", "Dairy", 3.99, 45);
        GroceryItemResponseDto responseDto = new GroceryItemResponseDto(1L, "Updated Milk", "Dairy", 3.99, 45);

        when(groceryItemService.updateGroceryItem(eq(1L), any(GroceryItemRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/grocery-items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Milk"))
                .andExpect(jsonPath("$.price").value(3.99));
    }

    @Test
    void deleteGroceryItem_Success_Returns204() throws Exception {
        doNothing().when(groceryItemService).deleteGroceryItem(1L);

        mockMvc.perform(delete("/api/grocery-items/1"))
                .andExpect(status().isNoContent());
    }
}
