package com.crio.onlinegrocery.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class OrderRequestDto {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotEmpty(message = "At least one grocery item ID must be provided")
    private List<Long> groceryItemIds;

    private LocalDateTime orderDate;

    private Double totalPrice;

    public OrderRequestDto() {
    }

    public OrderRequestDto(Long customerId, List<Long> groceryItemIds) {
        this.customerId = customerId;
        this.groceryItemIds = groceryItemIds;
    }

    public OrderRequestDto(Long customerId, List<Long> groceryItemIds, LocalDateTime orderDate, Double totalPrice) {
        this.customerId = customerId;
        this.groceryItemIds = groceryItemIds;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<Long> getGroceryItemIds() {
        return groceryItemIds;
    }

    public void setGroceryItemIds(List<Long> groceryItemIds) {
        this.groceryItemIds = groceryItemIds;
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
