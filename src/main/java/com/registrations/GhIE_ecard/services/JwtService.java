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
    public String generateToken(String username){
        return Jwts.builder()
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
}
