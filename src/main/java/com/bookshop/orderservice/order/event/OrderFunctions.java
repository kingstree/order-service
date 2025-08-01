package com.bookshop.orderservice.order.event;

import java.util.function.Consumer;

import com.bookshop.orderservice.order.domain.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderFunctions {

	private static final Logger log = LoggerFactory.getLogger(OrderFunctions.class);

	@Bean
	public Consumer<Flux<OrderDispatchedMessage>> dispatchOrder(OrderService orderService) {
		return flux -> orderService.consumeOrderDispatchedEvent(flux)
				.doOnNext(order -> log.info("The order with id {} is dispatched", order.id()))
				.subscribe(); //리엑티브 스트림을 활성화하기 위해 구독한다, 구독자가 없으면 데이터가 흐르지 않음
	}

}
