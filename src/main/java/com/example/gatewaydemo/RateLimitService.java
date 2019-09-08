package com.example.gatewaydemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    @Autowired
    private RateLimitRepository repository;

    public RateLimit getValue(String key) {
        return repository.getRateLimitByClientId(key);
    }

}
