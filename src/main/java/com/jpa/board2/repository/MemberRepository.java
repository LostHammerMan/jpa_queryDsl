package com.jpa.board2.repository;

import com.jpa.board2.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, QuerydslPredicateExecutor<Member> {

//    JpaRepository<Member, Long> --> <멤버타입, 멤버아이디>

    // 스프링 data JPA 가 username 으로 매칭 , JPQL 이 아래와 같이 작성됨
    // select m from Member m where m.username = ?
    List<Member> findByUsername(String username);

}
