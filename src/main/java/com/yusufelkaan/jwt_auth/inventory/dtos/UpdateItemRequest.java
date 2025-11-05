package com.yusufelkaan.jwt_auth.inventory.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateItemRequest(
        String name,
        @Min(value = 0, message = "Quantity cannot be negative")
        Integer quantity,
        @Min(value = 0, message = "Price cannot be negative")
        Double price,
        @Size(max = 255, message = "Location is too long")
        String location
) {
}
