package com.jpa.board2;

import com.jpa.board2.entity.Hello;
import com.jpa.board2.entity.QHello;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
class Board2ApplicationTests {

	@Autowired
	EntityManager em;

	@Test
	void contextLoads() {
		Hello hello = new Hello();
		em.persist(hello);

		// queryDsl 사용시 쿼리 관련은 QType 사용
		JPAQueryFactory query = new JPAQueryFactory(em);
		QHello qHello = new QHello("hello"); // alias 설정
		QHello qHello1 = QHello.hello; // 위와 같음

		Hello result = query
				.selectFrom(qHello)
				.fetchOne();

		// 검증
		Assertions.assertThat(result).isEqualTo(hello);
		Assertions.assertThat(result.getId()).isEqualTo(hello.getId());
	}


}
