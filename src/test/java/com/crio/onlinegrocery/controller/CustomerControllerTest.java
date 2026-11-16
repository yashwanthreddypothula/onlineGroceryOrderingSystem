package com.crio.onlinegrocery.controller;

import com.crio.onlinegrocery.dto.CustomerRequestDto;
import com.crio.onlinegrocery.dto.CustomerResponseDto;
import com.crio.onlinegrocery.exception.ResourceNotFoundException;
import com.crio.onlinegrocery.service.CustomerService;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCustomer_ValidRequest_Returns201() throws Exception {
        CustomerRequestDto requestDto = new CustomerRequestDto("Alice Smith", "alice@example.com", "123 Main St", "1234567890");
        CustomerResponseDto responseDto = new CustomerResponseDto(1L, "Alice Smith", "alice@example.com", "123 Main St", "1234567890");

        when(customerService.createCustomer(any(CustomerRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice Smith"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void createCustomer_InvalidEmail_Returns400() throws Exception {
        CustomerRequestDto requestDto = new CustomerRequestDto("Alice Smith", "invalid-email", "123 Main St", "1234567890");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    @Test
    void getAllCustomers_Returns200() throws Exception {
        CustomerResponseDto responseDto = new CustomerResponseDto(1L, "Alice Smith", "alice@example.com", "123 Main St", "1234567890");
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(responseDto));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice Smith"));
    }

    @Test
    void getCustomerById_Found_Returns200() throws Exception {
        CustomerResponseDto responseDto = new CustomerResponseDto(1L, "Alice Smith", "alice@example.com", "123 Main St", "1234567890");
        when(customerService.getCustomerById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice Smith"));
    }

    @Test
    void getCustomerById_NotFound_Returns404() throws Exception {
        when(customerService.getCustomerById(99L)).thenThrow(new ResourceNotFoundException("Customer not found with id: 99"));

        mockMvc.perform(get("/api/customers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Customer not found with id: 99"));
    }

    @Test
    void updateCustomer_Success_Returns200() throws Exception {
        CustomerRequestDto requestDto = new CustomerRequestDto("Alice Updated", "alice@example.com", "456 New St", "9876543210");
        CustomerResponseDto responseDto = new CustomerResponseDto(1L, "Alice Updated", "alice@example.com", "456 New St", "9876543210");

        when(customerService.updateCustomer(eq(1L), any(CustomerRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"));
    }

    @Test
    void deleteCustomer_Success_Returns204() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());
    }
}
