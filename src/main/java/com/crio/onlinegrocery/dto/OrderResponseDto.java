package com.crio.onlinegrocery.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDto {

    private Long id;
    private CustomerResponseDto customer;
    private List<GroceryItemResponseDto> groceryItems;
    private LocalDateTime orderDate;
    private Double totalPrice;

    public OrderResponseDto() {
    }

    public OrderResponseDto(Long id, CustomerResponseDto customer, List<GroceryItemResponseDto> groceryItems, LocalDateTime orderDate, Double totalPrice) {
        this.id = id;
        this.customer = customer;
        this.groceryItems = groceryItems;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerResponseDto getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerResponseDto customer) {
        this.customer = customer;
    }

    public List<GroceryItemResponseDto> getGroceryItems() {
        return groceryItems;
    }

    public void setGroceryItems(List<GroceryItemResponseDto> groceryItems) {
        this.groceryItems = groceryItems;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
