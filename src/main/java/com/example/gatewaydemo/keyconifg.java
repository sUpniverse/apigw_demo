package com.example.gatewaydemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class keyconifg {

    private String CLIENTID = "clientId";
    private String IP = "ip";
    private String HOSTNAME = "hostName";

    @Value("${clientRateLimit.type}")
    private String type;

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> {

            if(type.equals(CLIENTID)) {
                return Mono.just(exchange.getRequest().getQueryParams().getFirst(CLIENTID));
            } else if(type.equals(IP)) {
                return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().toString());
            } else if(type.equals(HOSTNAME)){
                return Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
            } else {
                return Mono.just(exchange.getRequest().getId());
            }
        };
    }
}
