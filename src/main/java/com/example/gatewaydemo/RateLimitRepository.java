package com.example.gatewaydemo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateLimitRepository extends JpaRepository<RateLimit, String> {

    RateLimit getRateLimitByClientId(String s);
}
