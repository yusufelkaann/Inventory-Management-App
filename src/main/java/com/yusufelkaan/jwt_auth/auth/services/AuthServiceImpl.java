package com.yusufelkaan.jwt_auth.auth.services;

import com.yusufelkaan.jwt_auth.auth.dtos.*;
import com.yusufelkaan.jwt_auth.auth.entities.User;
import com.yusufelkaan.jwt_auth.auth.repos.UserRepository;
import com.yusufelkaan.jwt_auth.auth.utils.EmailUtils;
import com.yusufelkaan.jwt_auth.auth.utils.JwtUtils;
import com.yusufelkaan.jwt_auth.shared.dtos.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtils emailUtils;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailUtils emailUtils, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailUtils = emailUtils;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public ApiResponse<Void> register(RegisterRequest registerRequest) throws MessagingException {
        /*
         * 1. validate the request -> done in controller
         * 2. check if user already exists & is verified
         * 3. user exists but not verified or check if verification code is expired
         * 5. user don't exist -> create user -> generate verification code -> send email
         * -> save user -> send email with verification code
         * */

        Optional<User> optionalUser = userRepository.findByEmail(registerRequest.email());
        User user;
        // Check if user exists
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            // If user is verified, return error response
            if (user.getIsVerified()){
                return ApiResponse.<Void>builder()
                        .message("email already exists")
                        .success(false)
                        .build();
            } else if (user.getVerifyCodeExpiry().after(new Date())) {
                // user not verified but the code is not expired
                return ApiResponse.<Void>builder()
                        .message("email already exists")
                        .success(false)
                        .build();
            }
            // user not verified and the code is expired
            int verificationCode = (int) ((Math.random() + 1) * 100000);
            user.setEmail(registerRequest.email());
            user.setName(registerRequest.name());
            user.setPassword(passwordEncoder.encode(registerRequest.password()));
            user.setVerifyCode(String.valueOf(verificationCode));
            user.setRole("USER");
            user.setVerifyCodeExpiry(new Date(System.currentTimeMillis() + 3600000));
            user.setIsVerified(false);

            User savedUser = userRepository.save(user);
            // send email with verification code
            final String subject = "Please verify your email";
            // ToDo: Add your reference link here
            final String EMAIL_TEMPLATE = """
                    <html>
                    <body>
                        <h1>Email Verification</h1>
                        <p>Thank you for registering. Please use the following verification code to verify your email address:</p>
                        <h2>%s</h2>
                        <p>This code will expire in 10 minutes.</p>
                        <p>Best regards,<br/>Your Company</p>
                    </html>
                    """.formatted(verificationCode);

            emailUtils.sendEmail(new MailBody(savedUser.getEmail(), subject, EMAIL_TEMPLATE));


            return ApiResponse.<Void>builder()
                    .message("User registered succesfully")
                    .success(true)
                    .build();

        }
        int verificationCode = (int) ((Math.random()+1) * 100000);
        // User does not exist, create new user
        User newUser = User.builder()
                .name(registerRequest.name())
                .email(registerRequest.email())
                .isVerified(false)
                .password(passwordEncoder.encode(registerRequest.password()))
                .verifyCode(String.valueOf((int) ((Math.random() + 1) * 100000)))
                .verifyCodeExpiry(new Date(System.currentTimeMillis() + 3600000))
                .role("USER")
                .build();

        User savedUser = userRepository.save(newUser);
        // send email with verification code
        final String subject = "Please verify your email";
        // ToDo: Add your reference link here
        final String EMAIL_TEMPLATE = """
                    <html>
                    <body>
                        <h1>Email Verification</h1>
                        <p>Thank you for registering. Please use the following verification code to verify your email address:</p>
                        <h2>%s</h2>
                        <p>This code will expire in 10 minutes.</p>
                        <p>Best regards,<br/>Your Company</p>
                    </html>
                    """.formatted(verificationCode);

        emailUtils.sendEmail(new MailBody(savedUser.getEmail(), subject, EMAIL_TEMPLATE));


        return ApiResponse.<Void>builder()
                .message("User registered succesfully")
                .success(true)
                .build();
    }

    @Override
    public ApiResponse<AuthDto> login(LoginRequest loginRequest, HttpServletResponse response) {
        /*
        * validate the request -> done in controller
        * check if user exists
        * check if user is verified
        * */
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.email());
        User user;
        if (optionalUser.isEmpty()) {
            return ApiResponse.<AuthDto>builder()
                    .message("User not found")
                    .success(false)
                    .data(null)
                    .build();
        }

        // User exits
        user = optionalUser.get();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        if (user.getIsVerified() && authentication.isAuthenticated()) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("email", user.getEmail());
            claims.put("name", user.getName());

            String accessToken = jwtUtils.generateToken(claims, user, response, Token.ACCESS);
            String refreshToken = jwtUtils.generateToken(claims, user, response, Token.REFRESH);

            user.setRefreshToken(refreshToken);

            User savedUser = userRepository.save(user);

            AuthDto authPayload = AuthDto.builder()
                    .name(savedUser.getName())
                    .email(savedUser.getEmail())
                    .isVerified(Boolean.TRUE)
                    .role(savedUser.getRole())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            return ApiResponse.<AuthDto>builder()
                    .data(authPayload)
                    .message("User logged in successfully")
                    .success(Boolean.TRUE)
                    .build();
        }

        return ApiResponse.<AuthDto>builder()
                .message("User not authenticated")
                .success(false)
                .data(null)
                .build();
    }

    @Override
    public ApiResponse<Void> verify(String email, String verifyCode) {
        /*
        * Check if user exists
        *
        * */
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;
        if (optionalUser.isEmpty()) {
            return ApiResponse.<Void>builder()
                    .message("Invalid email or verify code")
                    .success(false)
                    .build();
        }

        user = optionalUser.get();
        // check if user is already verified
        if (user.getIsVerified()) {
            return ApiResponse.<Void>builder()
                    .message("User is already verified")
                    .success(false)
                    .build();
        }
        // check if verify code matches
        if (user.getVerifyCode().equals(verifyCode)) {
            if (user.getVerifyCodeExpiry().after(new Date())) {
                user.setIsVerified(true);
                user.setVerifyCode(null);
                user.setVerifyCodeExpiry(null);
                userRepository.save(user);
                return ApiResponse.<Void>builder()
                        .message("User verified successfully")
                        .success(true)
                        .build();
            } else {
                return ApiResponse.<Void>builder()
                        .message("Verify code expired")
                        .success(false)
                        .build();
            }
        } else {
            return ApiResponse.<Void>builder()
                    .message("Invalid email or verify code")
                    .success(false)
                    .build();
        }
    }
}
