package com.crio.onlinegrocery.service;

import com.crio.onlinegrocery.dto.CustomerRequestDto;
import com.crio.onlinegrocery.dto.CustomerResponseDto;
import com.crio.onlinegrocery.entity.Customer;
import com.crio.onlinegrocery.exception.DuplicateResourceException;
import com.crio.onlinegrocery.exception.ResourceNotFoundException;
import com.crio.onlinegrocery.repository.CustomerRepository;
import com.crio.onlinegrocery.service.impl.CustomerServiceImpl;
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
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer sampleCustomer;
    private CustomerRequestDto requestDto;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer(1L, "Alice Smith", "alice@example.com", "123 Main St", "1234567890");
        requestDto = new CustomerRequestDto("Alice Smith", "alice@example.com", "123 Main St", "1234567890");
    }

    @Test
    void createCustomer_Success() {
        when(customerRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

        CustomerResponseDto response = customerService.createCustomer(requestDto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Alice Smith", response.getName());
        assertEquals("alice@example.com", response.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void createCustomer_DuplicateEmail_ThrowsException() {
        when(customerRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> customerService.createCustomer(requestDto));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void getAllCustomers_Success() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(sampleCustomer));

        List<CustomerResponseDto> list = customerService.getAllCustomers();

        assertEquals(1, list.size());
        assertEquals("Alice Smith", list.get(0).getName());
    }

    @Test
    void getCustomerById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));

        CustomerResponseDto response = customerService.getCustomerById(1L);

        assertNotNull(response);
        assertEquals("Alice Smith", response.getName());
    }

    @Test
    void getCustomerById_NotFound_ThrowsException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(99L));
    }

    @Test
    void updateCustomer_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));
        when(customerRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(sampleCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

        CustomerResponseDto updated = customerService.updateCustomer(1L, requestDto);

        assertNotNull(updated);
        assertEquals("Alice Smith", updated.getName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));
        doNothing().when(customerRepository).delete(sampleCustomer);

        assertDoesNotThrow(() -> customerService.deleteCustomer(1L));
        verify(customerRepository, times(1)).delete(sampleCustomer);
    }
}
