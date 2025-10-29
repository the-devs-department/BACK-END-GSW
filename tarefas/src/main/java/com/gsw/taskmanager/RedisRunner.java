package com.gsw.taskmanager;

import com.gsw.taskmanager.service.PasswordResetService;
import com.gsw.taskmanager.service.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisRunner implements CommandLineRunner {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PasswordResetService service;

    @Override
    public  void run(String... args) {
        redisTemplate.opsForValue().set("test:key", "Hello Redis from Spring!");
        System.out.println("Stored value: "+ redisTemplate.opsForValue().get("test:key"));
    }
}
