package com.bookshop.orderservice.order.domain;

import com.bookshop.orderservice.book.Book;
import com.bookshop.orderservice.book.BookClient;
import com.bookshop.orderservice.order.event.OrderAcceptedMessage;
import com.bookshop.orderservice.order.event.OrderDispatchedMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
@Service
public class OrderService {


	private static final Logger log = LoggerFactory.getLogger(OrderService.class);
	private final BookClient bookClient;
	private final OrderRepository orderRepository;
	private final StreamBridge streamBridge;

	public OrderService(BookClient bookClient, StreamBridge streamBridge, OrderRepository orderRepository) {
		this.bookClient = bookClient;
		this.orderRepository = orderRepository;
		this.streamBridge = streamBridge;
	}

	public Flux<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	@Transactional
	public Mono<Order> submitOrder(String isbn, int quantity) {
		return bookClient.getBookByIsbn(isbn)//비동기 적으로 상품 서버에 책을 조회
				.map(book -> buildAcceptedOrder(book, quantity))//받은 데이터를 객체에 저장
				.defaultIfEmpty(buildRejectedOrder(isbn, quantity))//못받으면 거부 정보 처리
				.flatMap(orderRepository::save)//받은걸 가지고 주문 저장
				.doOnNext(this::publishOrderAcceptedEvent); // 주문 생성 이벤트를 발생
	}

	public static Order buildAcceptedOrder(Book book, int quantity) {
		return Order.of(book.isbn(), book.title() + " - " + book.author(),
				book.price(), quantity, OrderStatus.ACCEPTED);
	}

	public static Order buildRejectedOrder(String bookIsbn, int quantity) {
		return Order.of(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
	}

	private void publishOrderAcceptedEvent(Order order) {
		if (!order.status().equals(OrderStatus.ACCEPTED)) {
			return;
		}
		var orderAcceptedMessage = new OrderAcceptedMessage(order.id());
		log.info("Sending order accepted event with id: {}", order.id());
		var result = streamBridge.send("acceptOrder-out-0", orderAcceptedMessage);//래빗 	MQ로 보냄
		log.info("Result of sending data for order with id {}: {}", order.id(), result);
	}
	//래빗 MQ의 메시지 소비
	public Flux<Order> consumeOrderDispatchedEvent(Flux<OrderDispatchedMessage> flux) {//리엑티브 스트림을 입력으로 받음
		return flux
				.flatMap(message -> orderRepository.findById(message.orderId())//스트림으로 보낸 각 객체에 대해 데이터 베이스에서 해당 주문을 읽는다.
				.map(this::buildDispatchedOrder)
				.flatMap(orderRepository::save));
	}

	private Order buildDispatchedOrder(Order existingOrder) {
		return new Order(
				existingOrder.id(),
				existingOrder.bookIsbn(),
				existingOrder.bookName(),
				existingOrder.bookPrice(),
				existingOrder.quantity(),
				OrderStatus.DISPATCHED,
				existingOrder.createdDate(),
				existingOrder.lastModifiedDate(),
				existingOrder.version()
		);
	}

}
