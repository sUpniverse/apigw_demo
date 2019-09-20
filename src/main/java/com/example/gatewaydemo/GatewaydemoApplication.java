package com.example.gatewaydemo;

import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.function.Consumer;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewaydemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewaydemoApplication.class, args);
    }

    @Autowired
    keyconifg keyconifg;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("schema", p -> p.path("/**")
                        .filters(s ->  {
                            s.rewritePath("/api/schema/(?<segment>.*)","/${segment}");
                            s.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter()));
                            return s;
                        })
                        .uri("lb://schema"))
                .route("hello", p -> p.path("/api/hello/**")
                        .filters(s -> s.rewritePath("/api/hello/(?<segment>.*)","/${segment}"))
                        .uri("lb://hello"))
                .build();

    }

    @Bean
    RedisRateLimiter redisRateLimiter() {
        RedisRateLimiter redisRateLimiter = new RedisRateLimiter(1, 2);
        return redisRateLimiter;
    }


}
