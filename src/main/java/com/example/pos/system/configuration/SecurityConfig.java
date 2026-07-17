package com.example.pos.system.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.Arrays;
import java.util.List;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final JwtValidator jwtValidator;

    public SecurityConfig(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth

                                // ==========================
                                // PUBLIC APIs
                                // ==========================
                                .requestMatchers("/auth/admin/**").hasAuthority("ROLE_SUPER_ADMIN")

                                .requestMatchers("/auth/**").permitAll()

                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**"
                                ).permitAll()

                                .requestMatchers("/uploads/**").permitAll()

                                // ==========================
                                // STORE APIs
                                // ==========================

                                // Create Store
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/stores"
                                ).hasAuthority("ROLE_SUPER_ADMIN")

                                // Get All Stores
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/stores"
                                ).hasAuthority("ROLE_SUPER_ADMIN")

                                // Get Single Store
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/stores/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN"
                                )

                                // Update Store
                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/api/stores/**"
                                ).hasAuthority("ROLE_SUPER_ADMIN")

                                // Delete Store
                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/stores/**"
                                ).hasAuthority("ROLE_SUPER_ADMIN")

                                // Moderate Store
                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/api/stores/*/moderate"
                                ).hasAuthority("ROLE_SUPER_ADMIN")

                                // Store Admin APIs
                                .requestMatchers("/api/stores/admin/**")
                                .hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN"
                                )

                                // Employee Store
                                .requestMatchers("/api/stores/employee/**")
                                .authenticated()

                                // ==========================
                                // PRODUCT APIs
                                // ==========================

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/products"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_INVENTORY_MANAGER"
                                )

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/products/upload"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_INVENTORY_MANAGER"
                                )

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/products/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_BRANCH_CASHIER",
                                        "ROLE_INVENTORY_MANAGER"
                                )

                                .requestMatchers(
                                        HttpMethod.PATCH,
                                        "/api/products/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_INVENTORY_MANAGER"
                                )

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/products/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN"
                                )

                                // ==========================
// BRANCH APIs
// ==========================

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/branches"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN"
                                )

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/branches/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_BRANCH_CASHIER"
                                )

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/api/branches/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_BRANCH_CASHIER"
                                )

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/branches/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_BRANCH_CASHIER"
                                )

                                // ==========================
// ==========================
// INVENTORY APIs
// ==========================

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/inventories/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_INVENTORY_MANAGER"
                                )

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/inventories/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_BRANCH_CASHIER",
                                        "ROLE_INVENTORY_MANAGER"
                                )

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/api/inventories/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_INVENTORY_MANAGER"
                                )

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/inventories/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN"
                                )

                                // ==========================
// STOCK MOVEMENT APIs
// ==========================

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/stock-movements"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_INVENTORY_MANAGER"
                                )

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/stock-movements/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_INVENTORY_MANAGER"
                                )

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/stock-movements/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN"
                                )

                                // ==========================
// CUSTOMER APIs
// ==========================

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/customers"
                                )
                                .hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_BRANCH_CASHIER"
                                )

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/customers/**"
                                )
                                .hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_BRANCH_CASHIER"
                                )

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/api/customers/**"
                                )
                                .hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_BRANCH_CASHIER"
                                )

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/customers/**"
                                )
                                .hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_BRANCH_CASHIER"
                                )

                                // ==========================
// ORDER APIs
// ==========================

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/orders/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_BRANCH_CASHIER"
                                )

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/orders/**"
                                )
                                .hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_BRANCH_CASHIER"
                                )

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/api/orders/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_BRANCH_CASHIER"
                                )

                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/orders/**"
                                ).hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_BRANCH_CASHIER"
                                )

                                //Suppliers
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/suppliers/**"
                                )
                                .hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN"
                                )
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/suppliers/**"
                                )
                                .hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN"
                                )

                                //Payment Details
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/payments/**"
                                )
                                .hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_ACCOUNTANT"
                                )

                                //Purchase
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/purchases",
                                        "/api/purchases/**"
                                )
                                .hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_INVENTORY_MANAGER"
                                )

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/purchases",
                                        "/api/purchases/**"
                                )
                                .hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_INVENTORY_MANAGER",
                                        "ROLE_ACCOUNTANT"
                                )


                                //Dashboard
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/dashboard/**"
                                )
                                .hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN",
                                        "ROLE_BRANCH_MANAGER",
                                        "ROLE_BRANCH_CASHIER",
                                        "ROLE_INVENTORY_MANAGER",
                                        "ROLE_ACCOUNTANT"
                                )

                                // ==========================
// USER APIs
// ==========================


                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/users/profile"
                                )
                                .authenticated()

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/users"
                                )
                                .hasAuthority("ROLE_SUPER_ADMIN")


                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/users/**"
                                )
                                .hasAnyAuthority(
                                        "ROLE_SUPER_ADMIN",
                                        "ROLE_STORE_ADMIN"
                                )


                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/api/users/**"
                                )
                                .hasAuthority("ROLE_SUPER_ADMIN")


                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/users/**"
                                )
                                .hasAuthority("ROLE_SUPER_ADMIN")
                                // ==========================
                                // EVERYTHING ELSE
                                // ==========================

                                .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtValidator,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:3000"
        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}