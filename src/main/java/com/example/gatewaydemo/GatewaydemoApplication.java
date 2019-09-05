package com.example.gatewaydemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewaydemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewaydemoApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("schema", p -> p.path("/api/schema/**")
                        .filters(s -> s.rewritePath("/api/schema/(?<segment>.*)","/${segment}"))
                        .uri("lb://schema"))
                .route("hello", p -> p.path("/api/hello/**")
                        .filters(s -> s.rewritePath("/api/hello/(?<segment>.*)","/${segment}"))
                        .uri("lb://hello"))
                .build();

    }

}
