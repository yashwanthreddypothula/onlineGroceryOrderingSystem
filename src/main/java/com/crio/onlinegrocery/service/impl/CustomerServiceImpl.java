package com.crio.onlinegrocery.service.impl;

import com.crio.onlinegrocery.dto.CustomerRequestDto;
import com.crio.onlinegrocery.dto.CustomerResponseDto;
import com.crio.onlinegrocery.entity.Customer;
import com.crio.onlinegrocery.exception.DuplicateResourceException;
import com.crio.onlinegrocery.exception.ResourceNotFoundException;
import com.crio.onlinegrocery.repository.CustomerRepository;
import com.crio.onlinegrocery.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponseDto createCustomer(CustomerRequestDto requestDto) {
        if (customerRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateResourceException("Customer with email '" + requestDto.getEmail() + "' already exists");
        }

        Customer customer = new Customer();
        customer.setName(requestDto.getName());
        customer.setEmail(requestDto.getEmail());
        customer.setAddress(requestDto.getAddress());
        customer.setPhone(requestDto.getPhone());

        Customer savedCustomer = customerRepository.save(customer);
        return mapToResponseDto(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return mapToResponseDto(customer);
    }

    @Override
    public CustomerResponseDto updateCustomer(Long id, CustomerRequestDto requestDto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        // Check if new email is taken by someone else
        Optional<Customer> existingWithEmail = customerRepository.findByEmail(requestDto.getEmail());
        if (existingWithEmail.isPresent() && !existingWithEmail.get().getId().equals(id)) {
            throw new DuplicateResourceException("Email '" + requestDto.getEmail() + "' is already registered to another customer");
        }

        customer.setName(requestDto.getName());
        customer.setEmail(requestDto.getEmail());
        customer.setAddress(requestDto.getAddress());
        customer.setPhone(requestDto.getPhone());

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToResponseDto(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
    }

    private CustomerResponseDto mapToResponseDto(Customer customer) {
        return new CustomerResponseDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getPhone()
        );
    }
}
