package com.yusufelkaan.jwt_auth.shared.dtos;

import lombok.*;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@Getter
@ToString
public final class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;
}
