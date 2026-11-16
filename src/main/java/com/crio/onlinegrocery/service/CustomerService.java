package com.crio.onlinegrocery.service;

import com.crio.onlinegrocery.dto.CustomerRequestDto;
import com.crio.onlinegrocery.dto.CustomerResponseDto;

import java.util.List;

public interface CustomerService {
    CustomerResponseDto createCustomer(CustomerRequestDto customerRequestDto);
    List<CustomerResponseDto> getAllCustomers();
    CustomerResponseDto getCustomerById(Long id);
    CustomerResponseDto updateCustomer(Long id, CustomerRequestDto customerRequestDto);
    void deleteCustomer(Long id);
}
