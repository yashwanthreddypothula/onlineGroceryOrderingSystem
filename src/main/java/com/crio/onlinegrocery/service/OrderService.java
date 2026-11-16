package com.crio.onlinegrocery.service;

import com.crio.onlinegrocery.dto.OrderRequestDto;
import com.crio.onlinegrocery.dto.OrderResponseDto;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto requestDto);
    List<OrderResponseDto> getAllOrders();
    OrderResponseDto getOrderById(Long id);
    List<OrderResponseDto> getOrdersByCustomerId(Long customerId);
    OrderResponseDto updateOrder(Long id, OrderRequestDto requestDto);
    void deleteOrder(Long id);
}
