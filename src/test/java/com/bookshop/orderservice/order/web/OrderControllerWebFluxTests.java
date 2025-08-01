package com.bookshop.orderservice.order.web;

import com.bookshop.orderservice.config.SecurityConfig;
import com.bookshop.orderservice.order.domain.Order;
import com.bookshop.orderservice.order.domain.OrderService;
import com.bookshop.orderservice.order.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebFluxTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerWebFluxTests {

	@Autowired
	WebTestClient webClient;

	@MockBean
	OrderService orderService;

	@Test
	void whenBookNotAvailableThenRejectOrder() {
		var orderRequest = new OrderRequest("1234567890", 3);
		var expectedOrder = OrderService.buildRejectedOrder(orderRequest.isbn(), orderRequest.quantity());
		given(orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity()))
				.willReturn(Mono.just(expectedOrder));

		webClient
				.mutateWith(SecurityMockServerConfigurers.mockJwt()
						.authorities(new SimpleGrantedAuthority("ROLE_customer")))//ROLE_customer역할을 갖는 사용자에 대한 JWT형식의 모의 액세스 토큰을 HTTP요청에 추가
				.post()
				.uri("/orders")
				.bodyValue(orderRequest)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(Order.class).value(actualOrder -> {
					assertThat(actualOrder).isNotNull();
					assertThat(actualOrder.status()).isEqualTo(OrderStatus.REJECTED);
				});
	}

}
