package com.yusufelkaan.jwt_auth.inventory.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateItemRequest(
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        Integer quantity,
        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price cannot be negative")
        Double price,
        @Size(max = 225, message = "Location cannot be longer than 225 characters")
        String location
) {

}
