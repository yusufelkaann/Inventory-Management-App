package com.yusufelkaan.jwt_auth.inventory.dtos;

import lombok.*;

@NoArgsConstructor
@Getter
@ToString
@AllArgsConstructor
@Builder
public final class ItemDto {
    private Long id;
    private String name;
    private int quantity;
    private double price;
    private String location;
}
