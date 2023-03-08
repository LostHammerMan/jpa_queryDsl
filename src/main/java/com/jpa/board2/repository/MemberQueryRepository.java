package com.jpa.board2.repository;

import com.jpa.board2.dto.MemberSearchCondition;
import com.jpa.board2.dto.MemberTeamDto;
import com.jpa.board2.dto.QMemberTeamDto;
import com.jpa.board2.entity.QTeam;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.jpa.board2.entity.QMember.member;

// 구현쿼리가 복잡한 경우, 인터페이스가 아닌 구현체(클래스) 로 작성, 특정 API에 종속되어 있는 경우
@Repository
public class MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public MemberQueryRepository(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<MemberTeamDto> search(MemberSearchCondition condition){

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
