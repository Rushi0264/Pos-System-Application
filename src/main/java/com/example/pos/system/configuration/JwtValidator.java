package com.example.pos.system.configuration;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtValidator extends OncePerRequestFilter {


    SecretKey key = Keys.hmacShaKeyFor(
            JwtConstant.JWT_SECRET.getBytes()
    );


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )
            throws ServletException, IOException {


        String jwt = request.getHeader("Authorization");

        System.out.println("========== JWT DEBUG ==========");
        System.out.println("AUTH HEADER : " + jwt);


        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            System.out.println(
                    SecurityContextHolder.getContext()
                            .getAuthentication()
                            .getAuthorities()
            );
        } else {
            System.out.println("Authentication is null");
        }


        if(jwt != null && jwt.startsWith("Bearer ")) {


            jwt = jwt.substring(7);


            try {

                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload();


                String email = claims.get("email", String.class);


                Object authoritiesObj = claims.get("authorities");

                String authorities = "";

                if(authoritiesObj != null){
                    authorities = authoritiesObj.toString();
                }


                List<SimpleGrantedAuthority> authList =
                        Arrays.stream(authorities.split(","))
                                .map(String::trim)
                                .filter(role -> !role.isEmpty())
                                .map(SimpleGrantedAuthority::new)
                                .toList();
                System.out.println("JWT AUTHORITIES FROM TOKEN = " + authList);



                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                authList
                        );


                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
                System.out.println(SecurityContextHolder.getContext().getAuthentication());


                System.out.println(
                        "USER : " + email
                );

                System.out.println(
                        "AUTHORITIES : " + authList
                );

            } catch(Exception e){

                System.out.println("JWT ERROR : "+e.getMessage());

            }

        }


        System.out.println("REQUEST : " + request.getMethod() + " " + request.getRequestURI());
        System.out.println("AUTH : " + SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request,response);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.startsWith("/uploads/");
    }
}