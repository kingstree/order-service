package com.bookshop.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

	@Bean //webClientBuilder : webClient 빈을 만들기 위해 스프링 부트가 자동 설정한 객체
	WebClient webClient(ClientProperties clientProperties, WebClient.Builder webClientBuilder) {
		return webClientBuilder //<-- webClient의 베이스URL을 사용자 정의 속성을 통해 지정한 카탈로그 서비스 URL로 설정한다.
				.baseUrl(clientProperties.catalogServiceUri().toString())
				.build();
	}

}
