package com.yusufelkaan.jwt_auth.auth.utils;

import com.yusufelkaan.jwt_auth.auth.dtos.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private final String SECRET_KEY;
    private final long EXPIRATION_TIME;

    public JwtUtils(@Value("${jwt.secret}") String secretKey,
                   @Value("${jwt.expiration}") long expirationTime) {
        this.SECRET_KEY = secretKey;
        this.EXPIRATION_TIME = expirationTime;
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            HttpServletResponse response,
            Token tokenType
    ) {

        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        String token = Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey())
                .compact();

        Cookie cookie = new Cookie(tokenType.name(), token);
        cookie.setHttpOnly(true); // to prevent XSS attacks
        cookie.setSecure(false); // if set to true, only transmit cookie over https
        cookie.setPath("/"); // the cookie will be available for the entire domain
        cookie.setMaxAge((int) (EXPIRATION_TIME / 1000)); // Convert to seconds
        response.addCookie(cookie);

        return token;
    }

    public String getTokenFromCookie(HttpServletRequest request, Token tokentype) {
        Cookie cookie = WebUtils.getCookie(request, tokentype.name());
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    public void removeTokenFromCookie(HttpServletResponse response, Token tokentype) {
        Cookie cookie = new Cookie(tokentype.name(), null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // delete the cookie
        response.addCookie(cookie);
    }

    private SecretKey getSignInKey() {
        // decode SECRET_KEY which is base64 encoded
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token, UserDetails userDetails) throws JwtException {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser() // Create a new JwtParser
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
