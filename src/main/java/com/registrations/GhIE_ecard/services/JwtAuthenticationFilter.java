package com.registrations.GhIE_ecard.services;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.security.Security;
import java.util.Optional;

// this class serves as a request filter and ensures tokens are correct before access is granted
@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Dependency injections
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    public final HandlerExceptionResolver handlerExceptionResolver;

    public JwtAuthenticationFilter (JwtService jwtService, CustomUserDetailsService userDetailsService, HandlerExceptionResolver handlerExceptionResolver){
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      final String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer")){ // If there is no Header OR it doesn't start
          // with "Bearer ", move to the next filter (like Registration)
          filterChain.doFilter(request, response);
          return;
      }
      // Extract the token by first taking the bearer word out

        try {
            String jwt = authHeader.substring(7);
            String userName = jwtService.extractUsername(jwt); // call jwtService to get username of admin

            // check if we have a username and user is not logged in
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails =  this.userDetailsService.loadUserByUsername(userName);
                // Build the identity card after confirming token validity
                UsernamePasswordAuthenticationToken newAuth= new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,  // we won't use password anymore since we  have  token
                        userDetails.getAuthorities()
                );
                // Give the token extra details about the request (like IP address)
                newAuth.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Tell Spring this user is officially logged in
                SecurityContextHolder.getContext().setAuthentication(newAuth);

        } filterChain.doFilter(request,response); // move on to next filter
        } catch (Exception exception){
            // Use this handler to catch errors , e.g expired token
            handlerExceptionResolver.resolveException(request, response, null, exception);

        }
    }
}
