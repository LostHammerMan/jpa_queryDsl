package com.jpa.board2;

import com.jpa.board2.dto.MemberDto;
import com.jpa.board2.dto.QMemberDto;
import com.jpa.board2.dto.UserDto;
import com.jpa.board2.entity.Member;
import com.jpa.board2.entity.QMember;
import com.jpa.board2.entity.Team;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.jpa.board2.entity.QMember.member;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class QueryDslBasicTest2 {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    // 중급편

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

    // 동적 쿼리
    // 1 BooleanBuilder 사용 --> 실무에서 많이 사용
    @Test
    public void dynamicQuery_BooleanBuilder(){
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember1(String usernameCond, Integer ageCond) {

        BooleanBuilder builder = new BooleanBuilder();

        // 초기값을 정해주는 경우
        BooleanBuilder builder2 = new BooleanBuilder(member.username.eq(usernameCond));

        // usernameCond != null 라면 BooleanBuilder 에 and 조건을 넣어줌
        if (usernameCond != null){
            builder.and(member.username.eq(usernameCond));
        }

        if (ageCond != null){
            builder.and(member.age.eq(ageCond));
        }

        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    // where 사용한 다중 파라미터 --> 권장
    @Test
    public void dynamicQuery_WhereParam(){

        String usernameParam = "member1";
        Integer ageParam = null;

        List<Member> result = searchMember2(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);

    }

    private List<Member> searchMember2(String usernameCond, Integer ageCond) {

        return queryFactory
                .selectFrom(member)
                // where(조건1, 조건2..) -> 각 조건은 and 로 연결됨
//                .where(usernameEq(usernameCond), ageEq(ageCond))

                // 각각 존재하는 메서드 조립해서 하나로 사용하는 것도 가능
                .where(allEq(usernameCond, ageCond))
                .fetch();
    }

    // 기본 방법
    /*private Predicate usernameEq(String usernameCond) {
        if (usernameCond == null){
            return null;
        }else{
            return member.username.eq(usernameCond);
        }
    }*/

    // 3항 연산자 활용 - 간단한 경우
    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond != null ? member.username.eq(usernameCond) : null;
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }

    // 위 메서드를 조립하기 위해서는 Predicate 가 아닌 BooleanExpression 사용
    // 통합 메서드
    private BooleanExpression allEq(String usernameCond, Integer ageCond){
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }

    // 벌크 연산
    //
    @Test
//    @Commit
    public void bulkUpdate(){
        // count --> 영향을 받은 row 수

        // 영속성 컨텍스트
        // member1 = 10 --> DB member1
        // member2 = 20 --> DB member2
        // member3 = 30 --> DB member3
        // member4 = 40 --> DB member4

        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();
        System.out.println("count = " + count);

        em.flush();
        em.clear();

        // 예상 결과(DB 변화)
        //1 member1 = 10 --> 1 DB 비회원
        //2 member2 = 20 --> 2 DB 비회원
        //3 member3 = 30 --> 3 DB member3
        //4 member4 = 40 --> 4 DB member4

        // jpa 는 영속성 컨텍스트와 DB 상 데이터가 다른 경우 영속성 컨텍스트가 우선권을 가짐
         // --> 영속성 컨텍스트의 데이터 그대로 유지됨
        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();

        for (Member member1 : result){
            System.out.println("member1 = " + member1);
        }

        // 결과 - 영속성 컨텍스트의 값 그대로 유지되어 있음
       /* member1 = Member(id=3, username=member1, age=10)
        member1 = Member(id=4, username=member2, age=20)
        member1 = Member(id=5, username=member3, age=30)
        member1 = Member(id=6, username=member4, age=40)*/

        // 해결법
//        - 벌크 연산자 후  영속성 컨텍스트 초기화 - em.flush(); , em.clear();
        // 결과
       /* member1 = Member(id=3, username=비회원, age=10)
        member1 = Member(id=4, username=비회원, age=20)
        member1 = Member(id=5, username=member3, age=30)
        member1 = Member(id=6, username=member4, age=40)*/
    }

    // 더하기
    @Test
    public void bulkAdd(){
        queryFactory
                .update(member)
                .set(member.age, member.age.add(1))
                .execute();
    }

    // 곱하기
    @Test
    public void bulkMultiply(){
        queryFactory
                .update(member)
                .set(member.age, member.age.multiply(2))
                .execute();
    }

    // 삭제
    @Test
    public void bulkDelete(){
        long count = queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();

        System.out.println("count = " + count);
    }

    // SQL function 호출
    @Test
    public void sqlFunction(){

       /* // username 의 member라는 이름을 "M" 으로 변경 */

    List<String> result = queryFactory
            .select(Expressions.stringTemplate("function('regexp_replace', {0}, {1}, {2})",
             member.username, "member", "M"))
            .from(member)
            .fetch();

    for (String s : result){
        System.out.println("s = " + s);
    }

    }

    // 소문자로 변경
    @Test
    public void sqlFunction2(){
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
//                .where(member.username.eq(
//                        Expressions.stringTempl
//                        ate("function('lower', {0})", member.username)))
                // queryDsl 내장되어 방법
                .where(member.username.eq(member.username.lower()))
                .fetch();

        for (String s : result){
            System.out.println("s = " + s);
        }
    }
}
