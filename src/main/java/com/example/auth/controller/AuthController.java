package com.example.auth.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.jwt.util.JwtUtil;
import com.example.auth.service.JwtBlacklistService;

import reactor.core.publisher.Mono;

	@RestController
	@RequestMapping("/auth")
	public class AuthController {
	
	    private final JwtUtil jwtUtil;
	    private final JwtBlacklistService jwtBlacklistService;
	
	    // Constructor injection (BEST PRACTICE)
	    public AuthController(
	            JwtUtil jwtUtil,
	            JwtBlacklistService jwtBlacklistService
	    ) {
	        this.jwtUtil = jwtUtil;
	        this.jwtBlacklistService = jwtBlacklistService;
	    }
	
	    // ✅ LOGIN
	    @PostMapping("/login")
	    public Mono<Map<String, String>> login(
	            @RequestBody Map<String, String> request
	    ) {
	        String username = request.get("username");
	        String password = request.get("password");
	
	        // Simple credential check (demo purpose)
	        if (!"admin".equals(username) || !"admin".equals(password)) {
	            return Mono.error(
	                    new RuntimeException("Invalid credentials")
	            );
	        }
	
	        String token = jwtUtil.generateToken(username);
	
	        return Mono.just(Map.of("token", token));
	    }
	
	    // ✅ LOGOUT (JWT BLACKLIST)
	    @PostMapping("/logout")
	    public Mono<Void> logout(
	            @RequestHeader("Authorization") String authHeader
	    ) {
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            return Mono.error(
	                    new RuntimeException("Invalid Authorization header")
	            );
	        }
	
	        String token = authHeader.substring(7);
	
	        // Store token in Redis blacklist
	        return jwtBlacklistService.blacklist(token)
	                .then(); // return Mono<Void>
	    }
	}
