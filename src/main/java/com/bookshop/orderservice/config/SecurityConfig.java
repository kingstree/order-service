package com.bookshop.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
		return http
				.authorizeExchange(exchange -> exchange
						.pathMatchers("/actuator/**").permitAll()
						.anyExchange().authenticated()//모든 요청은 인증이 필요하다
				)
				//.oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt)
				.oauth2ResourceServer(resourceServer ->
						resourceServer.jwt(jwt -> {})
				)
				.requestCache(requestCacheSpec -> //각 요청은 액세스 토큰을 가지고 있어야 함 세션 캐시를 유지할 필요가 없음
						requestCacheSpec.requestCache(NoOpServerRequestCache.getInstance()))
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.build();
	}

}
