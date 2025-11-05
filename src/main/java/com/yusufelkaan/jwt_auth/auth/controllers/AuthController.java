package com.yusufelkaan.jwt_auth.auth.controllers;

import com.yusufelkaan.jwt_auth.auth.dtos.AuthDto;
import com.yusufelkaan.jwt_auth.auth.dtos.LoginRequest;
import com.yusufelkaan.jwt_auth.auth.dtos.RegisterRequest;
import com.yusufelkaan.jwt_auth.auth.dtos.VerifyCodeDto;
import com.yusufelkaan.jwt_auth.auth.services.AuthService;
import com.yusufelkaan.jwt_auth.shared.dtos.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest registerRequest) throws MessagingException {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) throws MessagingException {
        return ResponseEntity.ok(authService.login(loginRequest, response));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<Void>> verifyCode(@Valid @RequestBody VerifyCodeDto verifyCodeDto, HttpServletResponse response) throws MessagingException {
        String mail = verifyCodeDto.email();
        String code = verifyCodeDto.verifyCode();
        return ResponseEntity.ok(authService.verify(mail, code));
    }
}
