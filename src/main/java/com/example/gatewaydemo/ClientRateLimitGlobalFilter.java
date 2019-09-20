package com.example.gatewaydemo;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class ClientRateLimitGlobalFilter {

	private LoadingCache<String,Integer> rateLimitCache;

//	@PostConstruct
	public void init() {


		rateLimitCache = CacheBuilder.newBuilder()
				.maximumSize(100)
				.expireAfterWrite(1, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Integer>() {
					@Override
					public Integer load(String s) throws Exception {
						//Todo : DB상에 값이 존재하지 않을 경우,,
						return 11;
					}
				});
	}


	@Bean
	@Order(1)
	public GlobalFilter clientRateLimitFilter() {
	    return (exchange, chain) -> { 
	    	log.debug("ClientRateLimitFilter called");
//
//	    	//TODO: Currently the specified number only applied for single api gateway instance.
//			//1. Extract Access Token
//			String key = "urn:aud";
//			//2. get Client ID
//			Integer value = null;
//			try {
//				value = rateLimitCache.get(key);
//			} catch (ExecutionException e) {
//				e.printStackTrace();
//			}
//			log.debug("value : {}",value);
//			if(value < 1) {
//				throw new RuntimeException("Exceed Request");
//			}
//
//			rateLimitCache.asMap().replace(key,--value);
	    	
	    	return chain.filter(exchange);
	    };
	}

}