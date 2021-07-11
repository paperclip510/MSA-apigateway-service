package com.example.scg.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.net.HttpHeaders;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthenticationHeaderFilter extends AbstractGatewayFilterFactory<AuthenticationHeaderFilter.Config> {

	Environment env;

	public AuthenticationHeaderFilter(Environment env) {
		super(Config.class);
		this.env = env;
	}

	// login -> issue token -> users (with token) -> header(include token)
	@Override
	public GatewayFilter apply(Config config) {

		// 로그인 했을때 받았던 토큰 인증 처리
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();

			// 헤더에 포함된 토큰 정보 확인

			// 헤더에 인증 정보가 없다면 Unauthorized 오류 발생
			if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
			}

			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwt = authorizationHeader.replace("Bearer", "").trim();

			if (!isJwtValid(jwt)) {
				return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
			}

			return chain.filter(exchange);
		};
	}

	private boolean isJwtValid(String jwt) {
		// TODO JWT token 유효성 판단 
		boolean returnValue = false;

		String subject = null;

		try {
			subject = Jwts.parser()
					.setSigningKey(env.getProperty("token.secret"))
					.parseClaimsJws(jwt).getBody()
					.getSubject();

			// eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

		} catch (Exception e) {
			returnValue = false;
		}

		if (subject == null || subject.isEmpty()) {
			returnValue = false;
		}else {
			returnValue = true;
		}

		String userId = null;
		
		
		
		return returnValue;
	}

	// Mono : Spring5 web flux 데이터 처리단위(단일값) 참고:Flux(다중값)
	// Spring WebFlux internally uses Project Reactor and its publisher
	// implementations – Flux and Mono.
	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
		// TODO 인증 에러 반환
		// 기존 HttpServletResponse
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);

		log.error(err);

		return response.setComplete();
	}

	public static class Config {

	}

}
