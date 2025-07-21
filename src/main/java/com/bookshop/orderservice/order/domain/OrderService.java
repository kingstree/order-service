package com.bookshop.orderservice.order.domain;

import com.bookshop.orderservice.book.Book;
import com.bookshop.orderservice.book.BookClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

	private final BookClient bookClient;
	private final OrderRepository orderRepository;

	public OrderService(BookClient bookClient, OrderRepository orderRepository) {
		this.bookClient = bookClient;
		this.orderRepository = orderRepository;
	}

	public Flux<Order> getAllOrders() {
		return orderRepository.findAll();
	}// 여러개의 주문

	public Mono<Order> submitOrder(String isbn, int quantity) { // 하나의 주문
		return bookClient.getBookByIsbn(isbn)//주문 객체를 가지고 모노를 생성한다.
				.map(book -> buildAcceptedOrder(book, quantity))
				.defaultIfEmpty(buildRejectedOrder(isbn, quantity))
				.flatMap(orderRepository::save);
	}

	public static Order buildAcceptedOrder(Book book, int quantity) {
		return Order.of(book.isbn(), book.title() + " - " + book.author(),
				book.price(), quantity, OrderStatus.ACCEPTED);
	}

	public static Order buildRejectedOrder(String bookIsbn, int quantity) {
		return Order.of(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
	}

}
