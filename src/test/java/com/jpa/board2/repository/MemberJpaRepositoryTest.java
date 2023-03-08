package com.jpa.board2.repository;

import com.jpa.board2.dto.MemberSearchCondition;
import com.jpa.board2.dto.MemberTeamDto;
import com.jpa.board2.entity.Member;
import com.jpa.board2.entity.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    @Qualifier("memberRepository")
    private MemberRepositoryCustom memberRepository;

    // 순수 JPA 사용
    @Test
    public void basicTest_Jpa(){
        Member member = new Member("member1", 10);

        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

        List<Member> result1 = memberJpaRepository.findAll();
        assertThat(result1).containsExactly(member);

        List<Member> result2 = memberJpaRepository.findByUserName("member1");
        assertThat(result2).containsExactly(member);
    }

    // QueryDsl  로 변경
    @Test
    public void basicTest_QueryDsl(){
        Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

        // findAll 변경
        /*List<Member> result1 = memberJpaRepository.findAll();
        Assertions.assertThat(result1).containsExactly(member);*/

        List<Member> result1 = memberJpaRepository.findAll_QueryDsl();
        assertThat(result1).containsExactly(member);

        // findByUserName 변경
        List<Member> result2 = memberJpaRepository.findByUsername_QueryDsl("member1");
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

        List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(condition);

        assertThat(result).extracting("username").containsExactly("member3","member4");
    }

    // where 다중 파라미터 사용 test --> 권장 방법
    @Test
    public void searchTest2(){

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
        condition.setAgeGoe(20);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");



        List<MemberTeamDto> result = memberJpaRepository.searchByWhere(condition);

        assertThat(result).extracting("username").containsExactly("member3","member4");
    }

    @Test
//    public List<Member> searchMember(){
    public void searchMember(){

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
        condition.setAgeGoe(20);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");



        List<MemberTeamDto> result = memberJpaRepository.searchByWhere(condition);

        assertThat(result).extracting("username").containsExactly("member3","member4");
    }

    @Test
    public void searchPageSimple(){
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
//        condition.setTeamName("teamB");
        PageRequest pageRequest = PageRequest.of(0, 3);


        Page<MemberTeamDto> result = memberRepository.searchPageSimple(condition, pageRequest);

        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getContent()).extracting("username").containsExactly("member1", "member2", "member3");

    }
}