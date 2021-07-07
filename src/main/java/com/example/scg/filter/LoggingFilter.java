package com.example.scg.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {
	// Spring 5 webflux

	public LoggingFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {

		// Global Pre Filter
//		return (exchange, chain) -> {
//			ServerHttpRequest request = exchange.getRequest();
//			ServerHttpResponse response = exchange.getResponse();
//
//			log.info("Global PRE filter baseMessage: {}", config.getBaseMessage());
//
//			if (config.isPostLogger()) {
//				log.info("Global Filter Start : request id ->  {}", request.getId());
//			}
//			// Global Post Filter
//			// Mono Spring5 Web Flux 비동기방식 서버 단일값 전달 방식.
//			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//				if (config.isPostLogger()) {
//					log.info("Global Filter End : response code -> {}", response.getStatusCode());
//				}
//				log.info("Global POST filter : response code -> {}", response.getStatusCode());
//
//			}));
//		};
		
		// interface
		GatewayFilter filter = new OrderedGatewayFilter((exchange,chain)->{
			ServerHttpRequest request = exchange.getRequest();
			ServerHttpResponse response = exchange.getResponse();

			log.info("Logging PRE filter baseMessage: {}", config.getBaseMessage());

			if (config.isPostLogger()) {
				log.info("Logging Filter Start : request id ->  {}", request.getId());
			}
			// Global Post Filter
			// Mono Spring5 Web Flux 비동기방식 서버 단일값 전달 방식.
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				if (config.isPostLogger()) {
					log.info("Logging POST filter : response code -> {}", response.getStatusCode());
				}

			}));
		}, Ordered.LOWEST_PRECEDENCE); //실행 순서 
		
		return filter;
		
	}

	@Data
	public static class Config {
		// Put the configuration properties.
		private String baseMessage;
		private boolean preLogger;
		private boolean postLogger;
	}
}
