package com.example.gatewaydemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewaydemoApplication {

    @Value("${client-ratelimit.scope}")
    private String scope;

    private static final String LOCAL = "local";
    private static final String CLUSTER = "cluster";

    @Autowired
    RateLimitConfiguration RateLimitConfiguration;

    @Autowired
    RedisRateLimiter redisRateLimiter;

    @Autowired
    LocalRateLimitFilter localRateLimitFilter;

    public static void main(String[] args) {
        SpringApplication.run(GatewaydemoApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("schema", p -> p.path("/api/schema/**")
                        .filters(s ->  {
                            s.rewritePath("/api/schema/(?<segment>.*)","/${segment}");
                            setAdditoryFilter(s);
                            return s;
                        })
                        .uri("lb://schema"))
                .route("demo", p -> p.path("/api/demo/**")
                        .filters(s -> {
                            s.rewritePath("/api/demo/(?<segment>.*)","/${segment}");
                            setAdditoryFilter(s);
                            return s;
                        })
                        .uri("lb://demo"))
                .build();

    }

    private UriSpec setAdditoryFilter(GatewayFilterSpec f) {
        if(scope.equals(CLUSTER)) {
            f.requestRateLimiter(c -> {
                c.setRateLimiter(redisRateLimiter);
            });
        } else if(scope.equals(LOCAL)) {
            f.filter(localRateLimitFilter.apply(new LocalRateLimitFilter.Config()));
        }
        return f;
    }


}
