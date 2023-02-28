package com.jpa.board2;

import com.jpa.board2.dto.MemberDto;
import com.jpa.board2.dto.QMemberDto;
import com.jpa.board2.dto.UserDto;
import com.jpa.board2.entity.Member;
import com.jpa.board2.entity.QMember;
import com.jpa.board2.entity.Team;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.jpa.board2.entity.QMember.member;

@SpringBootTest
@Transactional
public class QueryDslBasicTest2 {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

    }

// 프로젝션과 결과 반환 - 기본

    // 프로젝션 대상이 하나 -> 일반적으로 타입 하나
    // member 를 그대로 넣어도 프로젝션이 하나에 해당
    @Test
    public void simpleProjection(){
        List<String> result = queryFactory
//                .select(member)
                .select(member.username)
                .from(member)
                .fetch();

        for (String s : result){
            System.out.println("s = " + s);
        }
    }

    // 프로젝션 대상이 2개 이상 - 튜플로 조회
    @Test
    public void tupleProjection(){
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();
        for (Tuple tuple : result){
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);

            System.out.println("username = " + username);
            System.out.println("age = " + age);
            System.out.println("==================");
        }
    }

    // 순수 JPA 에서 DTO 조회 - new operation 을 활용한 방법
    @Test
    public void findByDtoByJPQL(){
        List<MemberDto> result = em.createQuery("select new com.jpa.board2.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : result){
            System.out.println("memberDto = " + memberDto);
        }
    }

    // QueryDsl 빈 생성
    // 1) 프로퍼티 접근 -  기본 생성자 필요
    @Test
    public void findByDtoBySetter(){
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class
                        , member.username
                        , member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result){
            System.out.println("memberDto = " + memberDto);
        }
    }

    // 2) 필드 접근 - getter, setter 필요 없음
    @Test
    public void findByDtoByField(){
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class
                        , member.username
                        , member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result){
            System.out.println("memberDto = " + memberDto);
        }
    }

    // 3) 생성자 사용
    @Test
    public void findByDtoByConstructor(){
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class
                        , member.username
                        , member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result){
            System.out.println("memberDto = " + memberDto);
        }
    }

    // UserDto 사용
    @Test
    public void findUserDto(){
        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class
//                        , member.username
                        , member.username.as("name")
                        , member.age))
                .from(member)
                .fetch();

        for (UserDto userDto : result){
            System.out.println("UserDto = " + userDto);
        }

       /* 결과 - 필드가 매칭되지 않아 UserDto에서는 "name" 으로 작성함
        null로 출력됨 -> member.username.as("name") 로 변경
        UserDto = UserDto(name=null, age=10)
        UserDto = UserDto(name=null, age=20)
        UserDto = UserDto(name=null, age=30)
        UserDto = UserDto(name=null, age=40)*/
    }

    // 프로퍼티나, 필트 접근 생성 방식에서 이름이 다를 때 해결방안
    @Test
    public void findUserDto2() {
        QMember memberSub = new QMember("memberSub");

        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class
//                        , member.username
                        , member.username.as("name")
//                        , member.age))
                        , ExpressionUtils.as(JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub), "age")
                ))
                .from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("UserDto = " + userDto);
        }
    }

    @Test
    public void findDtoByQueryProjection(){
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result){
            System.out.println("memberDto = " + memberDto);
        }
    }
}
