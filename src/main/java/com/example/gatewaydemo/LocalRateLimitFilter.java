package com.example.gatewaydemo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class LocalRateLimitFilter extends AbstractGatewayFilterFactory<LocalRateLimitFilter.Config> {

	private static final int EXPIRED_TIME = 1;
	private static final int EXPIRED_REMAINING = 0;
	public static final String REMAINING_HEADER = "X-RateLimit-Remaining";
	public static final String REPLENISH_RATE_HEADER = "X-RateLimit-Replenish-Rate";
	public static final String BURST_CAPACITY_HEADER = "X-RateLimit-Burst-Capacity";

	private Cache<String,Integer> rateLimitCache;

	@PostConstruct
	public void init() {
		initCache();
	}

	@Value("${client-rateLimit.replenishRate}")
	private int replenishRate;

	@Value("${client-rateLimit.burstCapacity}")
	private int burstCapacity;


	public void initCache() {

		rateLimitCache = CacheBuilder.newBuilder().expireAfterWrite(EXPIRED_TIME,TimeUnit.SECONDS).build();
	}

	@Override
	public GatewayFilter apply(Config config) {
		return new OrderedGatewayFilter((exchange, chain) -> {
			log.debug("localΩRateLimitFilter called");

//			//TODO: Currently the specified number only applied for single api gateway instance.

			//1. Extract Access Token
			//2. get Client ID
			String value = String.valueOf(Calendar.getInstance().getTimeInMillis());


			log.debug("check the limit of : {}",value);

			//3. add cache
			rateLimitCache.asMap().put(value,1);

			log.debug("check the cache: {} data : {}",rateLimitCache.size(), rateLimitCache.asMap().get(value));


			//4. if value exceed cache size applying rateLimit
			if(rateLimitCache.size() > burstCapacity) {
				//4-1. set X-RateLimit-Remaining, X-RateLimit-Burst-Capacity, X-RateLimit-Replenish-Rate in Header
				this.setHeader(exchange, EXPIRED_REMAINING);
				StringBuilder sb = new StringBuilder();
				sb.append("Too many request, Ratelimit count : ").append(burstCapacity);
				throw new RuntimeException(sb.toString());
			}

			return chain.filter(exchange);
		}, 0);
	}


	public static class Config {

	}

	public void setHeader(ServerWebExchange exchange, long remaining) {
		exchange.getResponse().getHeaders().set(REMAINING_HEADER, String.valueOf(remaining));
		exchange.getResponse().getHeaders().set(REPLENISH_RATE_HEADER, String.valueOf(replenishRate));
		exchange.getResponse().getHeaders().set(BURST_CAPACITY_HEADER, String.valueOf(burstCapacity));
	}

}