package com.jpa.board2.repository;

import com.jpa.board2.dto.MemberSearchCondition;
import com.jpa.board2.dto.MemberTeamDto;
import com.jpa.board2.dto.QMemberTeamDto;
import com.jpa.board2.entity.Member;
import com.jpa.board2.entity.QTeam;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.jpa.board2.entity.QMember.member;

public class MemberRepositoryImpl implements MemberRepositoryCustom {


    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    // QuerydslRepositorySupport 사용
   /* public MemberRepositoryImpl(EntityManager em) {
        super(Member.class);
        this.queryFactory = new JPAQueryFactory(em);
    }
*/
    public List<MemberTeamDto> search(MemberSearchCondition condition){

//        EntityManager entityManager = getEntityManager();
        // QuerydslRepositorySupport 사용
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        QTeam.team.id.as("teamId"),
                        QTeam.team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, QTeam.team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamnameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())

                        // 조립하여 사용
//                        ageBetween(condition.getAgeLoe(), condition.getAgeGoe())
                )
                .fetch();
        /*List<MemberTeamDto> result = from(member)
                .leftJoin(member.team, QTeam.team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamnameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        QTeam.team.id.as("teamId"),
                        QTeam.team.name.as("teamName")
                ))
                .fetch();

        return result;*/
    }

    // 페이징 단순 쿼리
    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        QueryResults<MemberTeamDto> results = queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        QTeam.team.id.as("teamId"),
                        QTeam.team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, QTeam.team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamnameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())

                        // 조립하여 사용
//                        ageBetween(condition.getAgeLoe(), condition.getAgeGoe())
                )
                // 페이징
                .offset(pageable.getOffset()) // n 이전은 스킵하고, n 번부터 시작
                .limit(pageable.getPageSize()) // 한 번 조회할 때 데이터 수량
                .fetchResults(); // 컨텐츠용 쿼리, 카운트 쿼리 각각 날림

        List<MemberTeamDto> content = results.getResults(); // 실제 contents
        long total = results.getTotal(); // total count

        return new PageImpl<>(content, pageable, total);

    }

    // QuerydslRepositorySupport 사용한 페이징

    /*public Page<MemberTeamDto> searchPageSimple2(MemberSearchCondition condition, Pageable pageable) {
        JPQLQuery<MemberTeamDto> jpaQuery =
                from(member)
                .leftJoin(member.team, QTeam.team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamnameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())

                        // 조립하여 사용
//                        ageBetween(condition.getAgeLoe(), condition.getAgeGoe())
                )
                        .select(new QMemberTeamDto(
                                member.id.as("memberId"),
                                member.username,
                                member.age,
                                QTeam.team.id.as("teamId"),
                                QTeam.team.name.as("teamName")
                        ));
                // 페이징
                *//*.offset(pageable.getOffset()) // n 이전은 스킵하고, n 번부터 시작
                .limit(pageable.getPageSize()) // 한 번 조회할 때 데이터 수량
                .fetchResults(); // 컨텐츠용 쿼리, 카운트 쿼리 각각 날림*//*

        JPQLQuery<MemberTeamDto> query = getQuerydsl().applyPagination(pageable, jpaQuery);

        query.fetch();

      *//*  List<MemberTeamDto> content = results.getResults(); // 실제 contents
        long total = results.getTotal(); // total count*//*

        return new PageImpl<>(content, pageable, total);

    }*/

    // 페이징 복잡 쿼리
    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        QTeam.team.id.as("teamId"),
                        QTeam.team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, QTeam.team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamnameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())

                        // 조립하여 사용
//                        ageBetween(condition.getAgeLoe(), condition.getAgeGoe())
                )
                // 페이징
                .offset(pageable.getOffset()) // n 이전은 스킵하고, n 번부터 시작
                .limit(pageable.getPageSize()) // 한 번 조회할 때 데이터 수량
                .fetch(); // 컨텐츠용 쿼리, 카운트 쿼리 각각 날림

        // totalCount 용 쿼리 최적화 목적 --> 때에 따라 생략 가능
        long totalCount = queryFactory
                .select(member)
                .from(member)
                .leftJoin(member.team, QTeam.team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamnameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .fetchCount();

        // totalCount 용 쿼리 최적화 목적 --> 때에 따라 생략 가능
        JPAQuery<Member> countQuery = queryFactory
                .select(member)
                .from(member)
                .leftJoin(member.team, QTeam.team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamnameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                );


//        return new PageImpl<>(content, pageable, totalCount);

        // count 쿼리 최적화 하는 경우
//        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchCount());
         // 위와 같은 표현
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    // 후에 메서드 조합을 위해 BooleanExpression 사용
    private BooleanExpression usernameEq(String username) {
        return StringUtils.hasText(username) ? member.username.eq(username) : null;
    }

    private BooleanExpression teamnameEq(String teamName) {
        return StringUtils.hasText(teamName) ? QTeam.team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }

    private BooleanExpression ageBetween(int ageLoe, int ageGoe){
        return ageGoe(ageLoe).and(ageGoe(ageGoe));
    }


}
