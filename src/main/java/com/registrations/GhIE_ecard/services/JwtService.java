package com.registrations.GhIE_ecard.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    // SECRET KEY to sign in GhIE admins
    @Value("${jwt.secret}")
    private String secret_key;

    //  A helper to turn that String into a real Security Key
    private Key getSigninKey(){
       byte[] keyBytes = Decoders.BASE64.decode(secret_key);
       return Keys.hmacShaKeyFor(keyBytes);
    }

    // method to generate key for admins
    /*
    overload generateToken so it accepts custom claim maps,
    with this , we can now add other validation parameters for admins to sign in
     */
    public String generateToken(Map<String, Object> extraClaims, String username){
        return Jwts.builder()
                .setClaims(extraClaims) // This loads the custom map (institution, etc.) into the payload
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10 ))// 10 hour expiration
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    // method to extract username from token
    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token) //
                .getBody()
                .getSubject();
    }

    // New: Extract role directly from token claims
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // New: Generic helper to extract any claim from token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // New: Helper to parse and extract all claims from token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

