package com.practice.franquicias_management_api.web;

import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(-1)
public class RequestLoggingWebFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		if (isSwaggerPath(exchange.getRequest())) {
			return chain.filter(exchange);
		}
		long start = System.currentTimeMillis();
		ServerHttpRequest request = exchange.getRequest();
		String method = request.getMethod().name();
		String path = request.getURI().getRawPath();
		String query = request.getURI().getRawQuery();
		String target = query == null ? path : path + "?" + query;
		log.info("HTTP {} {}", method, target);
		return chain.filter(exchange)
				.doOnSuccess(v -> log.info(
						"HTTP {} {} -> {} ({} ms)",
						method,
						target,
						exchange.getResponse().getStatusCode(),
						System.currentTimeMillis() - start))
				.doOnError(error -> log.error(
						"HTTP {} {} -> error: {} ({} ms)",
						method,
						target,
						error.getMessage(),
						System.currentTimeMillis() - start));
	}

	private static boolean isSwaggerPath(ServerHttpRequest request) {
		String path = request.getURI().getPath();
		return path.startsWith("/swagger-ui")
				|| path.startsWith("/v3/api-docs")
				|| path.startsWith("/webjars/swagger-ui");
	}
}
