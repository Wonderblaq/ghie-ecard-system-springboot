package com.registrations.GhIE_ecard.security_config;
import com.registrations.GhIE_ecard.services.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    public final JwtAuthenticationFilter jwtFilter;

    // Inject the created jwtFilter before spring can check username and password
    public SecurityConfig(JwtAuthenticationFilter jwtFilter){
        this.jwtFilter = jwtFilter;
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow the React dev's local machine, port number can / must be changed
        configuration.setAllowedOrigins(List.of("*"));

        // Allow standard HTTP actions
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Allow the JWT header!
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers for now
        configuration.setAllowCredentials(true);      // Often required for browser-to-cloud requests

        // Apply this to ALL endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //  Enable CORS first!
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                //  Disable CSRF for your stateless API
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // EXPLICITLY permit all OPTIONS requests
                        .requestMatchers(org.springframework.web.bind.annotation.RequestMethod.OPTIONS.name(), "/**").permitAll()
                        .requestMatchers("/registration/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();  // This algorithm enables spring to encode
        // password in database from normal plain readable texts into encoded scripts
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



}
