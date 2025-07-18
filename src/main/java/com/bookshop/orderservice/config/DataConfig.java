package com.bookshop.orderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@Configuration // 이 클래스가 스프링 설정을 위한 클레스임을 나타낸다.
@EnableR2dbcAuditing
public class DataConfig { // 지속성 엔티티에 대한 R2DBC 감사를 활성화한다.
}
