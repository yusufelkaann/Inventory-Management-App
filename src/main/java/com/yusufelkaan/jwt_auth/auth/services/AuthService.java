package com.yusufelkaan.jwt_auth.auth.services;

import com.yusufelkaan.jwt_auth.auth.dtos.AuthDto;
import com.yusufelkaan.jwt_auth.auth.dtos.LoginRequest;
import com.yusufelkaan.jwt_auth.auth.dtos.RegisterRequest;
import com.yusufelkaan.jwt_auth.shared.dtos.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    ApiResponse<Void> register(RegisterRequest registerRequest) throws MessagingException;

    ApiResponse<AuthDto> login(LoginRequest loginRequest, HttpServletResponse response);

    ApiResponse<Void> verify(String email, String verifyCode);


}
