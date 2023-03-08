package com.jpa.board2.repository;

import com.jpa.board2.dto.MemberSearchCondition;
import com.jpa.board2.dto.MemberTeamDto;
import com.jpa.board2.entity.Member;
import com.jpa.board2.entity.QMember;
import com.jpa.board2.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    // JPA DATA 사용, 엥간한거 거진 다 지원되어 구현체 만들어줌
    @Test
    public void basicTest_Jpa(){
        Member member = new Member("member1", 10);

        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

        List<Member> result1 = memberRepository.findAll();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberRepository.findByUsername("member1");
        assertThat(result2).containsExactly(member);
    }

    @Test
    public void searchTest(){

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

        MemberSearchCondition condition = new MemberSearchCondition();
//        condition.setAgeGoe(35);
//        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        // 위 조건이 모두 빠진 경우 모든 데이터를 가져옴 ->limit 걸어두는 것이 좋음

        List<MemberTeamDto> result = memberRepository.search(condition);

        assertThat(result).extracting("username").containsExactly("member3","member4");
    }

    // QuerydslPredicateExecutor
    @Test
    public void querydslPredicateExecutorTest(){
        QMember member = QMember.member;
        Iterable<Member> result = memberRepository.findAll(member.age.between(10, 40)
                .and(member.username.eq("member1")));

        for (Member findMember : result){
            System.out.println("member1 = " + findMember);
        }
    }
}