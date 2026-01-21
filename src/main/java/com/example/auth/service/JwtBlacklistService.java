package com.example.auth.service;

import java.time.Duration;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.auth.config.JwtProperties;

import reactor.core.publisher.Mono;

@Service
public class JwtBlacklistService {
	
	private final ReactiveStringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

    public JwtBlacklistService(
        ReactiveStringRedisTemplate redisTemplate,
        JwtProperties jwtProperties
    ) {
        this.redisTemplate = redisTemplate;
        this.jwtProperties = jwtProperties;
    }

    public Mono<Boolean> blacklist(String token) {
        long ttl = jwtProperties.getExpiration();
        return redisTemplate.opsForValue()
            .set(token, "BLACKLISTED", Duration.ofMillis(ttl));
    }

}
