package com.crio.onlinegrocery.controller;

import com.crio.onlinegrocery.dto.CustomerResponseDto;
import com.crio.onlinegrocery.dto.GroceryItemResponseDto;
import com.crio.onlinegrocery.dto.OrderRequestDto;
import com.crio.onlinegrocery.dto.OrderResponseDto;
import com.crio.onlinegrocery.exception.ResourceNotFoundException;
import com.crio.onlinegrocery.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrder_ValidRequest_Returns201() throws Exception {
        OrderRequestDto requestDto = new OrderRequestDto(1L, Arrays.asList(1L, 2L));
        CustomerResponseDto customerDto = new CustomerResponseDto(1L, "Alice Smith", "alice@example.com", "123 Main St", "1234567890");
        GroceryItemResponseDto itemDto = new GroceryItemResponseDto(1L, "Milk", "Dairy", 3.50, 10);
        OrderResponseDto responseDto = new OrderResponseDto(1L, customerDto, Collections.singletonList(itemDto), LocalDateTime.now(), 3.50);

        when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalPrice").value(3.50))
                .andExpect(jsonPath("$.customer.name").value("Alice Smith"));
    }

    @Test
    void createOrder_MissingCustomerId_Returns400() throws Exception {
        OrderRequestDto requestDto = new OrderRequestDto(null, Arrays.asList(1L));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.customerId").exists());
    }

    @Test
    void createOrder_EmptyItemsList_Returns400() throws Exception {
        OrderRequestDto requestDto = new OrderRequestDto(1L, Collections.emptyList());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.groceryItemIds").exists());
    }

    @Test
    void getAllOrders_Returns200() throws Exception {
        CustomerResponseDto customerDto = new CustomerResponseDto(1L, "Alice Smith", "alice@example.com", "123 Main St", "1234567890");
        OrderResponseDto responseDto = new OrderResponseDto(1L, customerDto, Collections.emptyList(), LocalDateTime.now(), 0.0);
        when(orderService.getAllOrders()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getOrderById_Found_Returns200() throws Exception {
        CustomerResponseDto customerDto = new CustomerResponseDto(1L, "Alice Smith", "alice@example.com", "123 Main St", "1234567890");
        OrderResponseDto responseDto = new OrderResponseDto(1L, customerDto, Collections.emptyList(), LocalDateTime.now(), 15.0);
        when(orderService.getOrderById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalPrice").value(15.0));
    }

    @Test
    void getOrderById_NotFound_Returns404() throws Exception {
        when(orderService.getOrderById(99L)).thenThrow(new ResourceNotFoundException("Order not found with id: 99"));

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Order not found with id: 99"));
    }

    @Test
    void getOrdersByCustomerId_Returns200() throws Exception {
        CustomerResponseDto customerDto = new CustomerResponseDto(1L, "Alice Smith", "alice@example.com", "123 Main St", "1234567890");
        OrderResponseDto responseDto = new OrderResponseDto(1L, customerDto, Collections.emptyList(), LocalDateTime.now(), 20.0);
        when(orderService.getOrdersByCustomerId(1L)).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/orders/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void updateOrder_Success_Returns200() throws Exception {
        OrderRequestDto requestDto = new OrderRequestDto(1L, Arrays.asList(1L));
        CustomerResponseDto customerDto = new CustomerResponseDto(1L, "Alice Smith", "alice@example.com", "123 Main St", "1234567890");
        OrderResponseDto responseDto = new OrderResponseDto(1L, customerDto, Collections.emptyList(), LocalDateTime.now(), 10.0);

        when(orderService.updateOrder(eq(1L), any(OrderRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(10.0));
    }

    @Test
    void deleteOrder_Success_Returns204() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());
    }
}
