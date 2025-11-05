package com.yusufelkaan.jwt_auth.auth.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public final class AuthDto {
    private String name;
    private String email;
    private String accessToken;
    private String refreshToken;
    private Boolean isVerified;
    private String role;

}
