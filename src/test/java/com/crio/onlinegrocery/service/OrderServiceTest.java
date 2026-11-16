package com.crio.onlinegrocery.service;

import com.crio.onlinegrocery.dto.OrderRequestDto;
import com.crio.onlinegrocery.dto.OrderResponseDto;
import com.crio.onlinegrocery.entity.Customer;
import com.crio.onlinegrocery.entity.GroceryItem;
import com.crio.onlinegrocery.entity.Order;
import com.crio.onlinegrocery.exception.ResourceNotFoundException;
import com.crio.onlinegrocery.repository.CustomerRepository;
import com.crio.onlinegrocery.repository.GroceryItemRepository;
import com.crio.onlinegrocery.repository.OrderRepository;
import com.crio.onlinegrocery.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private GroceryItemRepository groceryItemRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer sampleCustomer;
    private GroceryItem sampleItem1;
    private GroceryItem sampleItem2;
    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer(1L, "Alice Smith", "alice@example.com", "123 Main St", "1234567890");
        sampleItem1 = new GroceryItem(1L, "Milk", "Dairy", 3.50, 10);
        sampleItem2 = new GroceryItem(2L, "Bread", "Bakery", 2.50, 15);
        sampleOrder = new Order(1L, sampleCustomer, Arrays.asList(sampleItem1, sampleItem2), LocalDateTime.now(), 6.00);
    }

    @Test
    void createOrder_Success_AutoCalculatesPrice() {
        OrderRequestDto requestDto = new OrderRequestDto(1L, Arrays.asList(1L, 2L));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));
        when(groceryItemRepository.findById(1L)).thenReturn(Optional.of(sampleItem1));
        when(groceryItemRepository.findById(2L)).thenReturn(Optional.of(sampleItem2));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        OrderResponseDto response = orderService.createOrder(requestDto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(6.00, response.getTotalPrice());
        assertEquals(2, response.getGroceryItems().size());
        assertEquals(1L, response.getCustomer().getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_CustomerNotFound_ThrowsException() {
        OrderRequestDto requestDto = new OrderRequestDto(99L, Arrays.asList(1L));

        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(requestDto));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_GroceryItemNotFound_ThrowsException() {
        OrderRequestDto requestDto = new OrderRequestDto(1L, Arrays.asList(99L));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));
        when(groceryItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(requestDto));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getAllOrders_Success() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(sampleOrder));

        List<OrderResponseDto> orders = orderService.getAllOrders();

        assertEquals(1, orders.size());
        assertEquals(1L, orders.get(0).getId());
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        OrderResponseDto order = orderService.getOrderById(1L);

        assertNotNull(order);
        assertEquals(1L, order.getId());
        assertEquals(6.00, order.getTotalPrice());
    }

    @Test
    void getOrdersByCustomerId_Success() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findByCustomerId(1L)).thenReturn(Collections.singletonList(sampleOrder));

        List<OrderResponseDto> orders = orderService.getOrdersByCustomerId(1L);

        assertEquals(1, orders.size());
        assertEquals(1L, orders.get(0).getCustomer().getId());
    }

    @Test
    void deleteOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        doNothing().when(orderRepository).delete(sampleOrder);

        assertDoesNotThrow(() -> orderService.deleteOrder(1L));
        verify(orderRepository, times(1)).delete(sampleOrder);
    }
}
