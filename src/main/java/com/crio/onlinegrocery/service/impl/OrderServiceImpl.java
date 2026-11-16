package com.crio.onlinegrocery.service.impl;

import com.crio.onlinegrocery.dto.CustomerResponseDto;
import com.crio.onlinegrocery.dto.GroceryItemResponseDto;
import com.crio.onlinegrocery.dto.OrderRequestDto;
import com.crio.onlinegrocery.dto.OrderResponseDto;
import com.crio.onlinegrocery.entity.Customer;
import com.crio.onlinegrocery.entity.GroceryItem;
import com.crio.onlinegrocery.entity.Order;
import com.crio.onlinegrocery.exception.ResourceNotFoundException;
import com.crio.onlinegrocery.repository.CustomerRepository;
import com.crio.onlinegrocery.repository.GroceryItemRepository;
import com.crio.onlinegrocery.repository.OrderRepository;
import com.crio.onlinegrocery.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final GroceryItemRepository groceryItemRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CustomerRepository customerRepository,
                            GroceryItemRepository groceryItemRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.groceryItemRepository = groceryItemRepository;
    }

    @Override
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        Customer customer = customerRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + requestDto.getCustomerId()));

        List<GroceryItem> items = resolveGroceryItems(requestDto.getGroceryItemIds());

        LocalDateTime orderDate = requestDto.getOrderDate() != null ? requestDto.getOrderDate() : LocalDateTime.now();
        Double totalPrice = calculateTotalPrice(requestDto.getTotalPrice(), items);

        Order order = new Order();
        order.setCustomer(customer);
        order.setGroceryItems(items);
        order.setOrderDate(orderDate);
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);
        return mapToResponseDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponseDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto updateOrder(Long id, OrderRequestDto requestDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        Customer customer = customerRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + requestDto.getCustomerId()));

        List<GroceryItem> items = resolveGroceryItems(requestDto.getGroceryItemIds());

        LocalDateTime orderDate = requestDto.getOrderDate() != null ? requestDto.getOrderDate() : order.getOrderDate();
        Double totalPrice = calculateTotalPrice(requestDto.getTotalPrice(), items);

        order.setCustomer(customer);
        order.setGroceryItems(items);
        order.setOrderDate(orderDate);
        order.setTotalPrice(totalPrice);

        Order updatedOrder = orderRepository.save(order);
        return mapToResponseDto(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        orderRepository.delete(order);
    }

    private List<GroceryItem> resolveGroceryItems(List<Long> itemIds) {
        List<GroceryItem> items = new ArrayList<>();
        for (Long itemId : itemIds) {
            GroceryItem item = groceryItemRepository.findById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Grocery item not found with id: " + itemId));
            items.add(item);
        }
        return items;
    }

    private Double calculateTotalPrice(Double requestedTotal, List<GroceryItem> items) {
        if (requestedTotal != null && requestedTotal > 0) {
            return Math.round(requestedTotal * 100.0) / 100.0;
        }
        double sum = items.stream().mapToDouble(GroceryItem::getPrice).sum();
        return Math.round(sum * 100.0) / 100.0;
    }

    private OrderResponseDto mapToResponseDto(Order order) {
        CustomerResponseDto customerDto = null;
        if (order.getCustomer() != null) {
            Customer c = order.getCustomer();
            customerDto = new CustomerResponseDto(c.getId(), c.getName(), c.getEmail(), c.getAddress(), c.getPhone());
        }

        List<GroceryItemResponseDto> itemDtos = new ArrayList<>();
        if (order.getGroceryItems() != null) {
            itemDtos = order.getGroceryItems().stream()
                    .map(item -> new GroceryItemResponseDto(item.getId(), item.getName(), item.getCategory(), item.getPrice(), item.getQuantity()))
                    .collect(Collectors.toList());
        }

        return new OrderResponseDto(
                order.getId(),
                customerDto,
                itemDtos,
                order.getOrderDate(),
                order.getTotalPrice()
        );
    }
}
