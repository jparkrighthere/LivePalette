package com.example.demo.config;

import com.example.demo.auth.model.RefreshToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        //기본적으로 host=Localhost, port=6379
        return new LettuceConnectionFactory("redis", 6379);
    }
    @Bean
    public RedisTemplate<String, RefreshToken> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, RefreshToken> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //key Serializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //Value Serializer
        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer<>(RefreshToken.class,objectMapper);
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
