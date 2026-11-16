package com.crio.onlinegrocery.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CustomerRequestDto {

    @NotBlank(message = "Customer name is required")
    private String name;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    private String email;

    @NotBlank(message = "Customer address is required")
    private String address;

    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^[0-9+()\\s-]{7,20}$", message = "Customer phone must be valid (7 to 20 digits/symbols)")
    private String phone;

    public CustomerRequestDto() {
    }

    public CustomerRequestDto(String name, String email, String address, String phone) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
