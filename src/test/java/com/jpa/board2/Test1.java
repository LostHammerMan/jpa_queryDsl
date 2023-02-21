package com.jpa.board2;

import com.jpa.board2.domain.Member;
import com.jpa.board2.domain.Team;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Test1 {

    @Autowired
    private EntityManager em;

    @Test
    @Transactional
    public void 회원저장() throws Exception {
        // given

        // 팀1 저장
        Team team = new Team();
        em.persist(team);

        // 멤버1 저장
        Member member1 = new Member();
        member1.setName("kim");
        member1.setTeam(team);
        em.persist(member1);
        // 멤버 2 저장
        Member member2 = new Member();
        member2.setName("LEE");
        member2.setTeam(team);
        em.persist(member2);

        // when

        // then
        Assert.assertEquals(member2.getName(), "LEE");
    }

    @Test
    public void 회원조회() throws Exception {
        // given
        // 팀1 저장
        Team team = new Team();
        em.persist(team);

        // 멤버1 저장
        Member member1 = new Member();
        member1.setName("kim");
        member1.setTeam(team);
        em.persist(member1);
        // when
        Member findMember = em.find(Member.class, "1");
        Team findTeam = findMember.getTeam();// 객체 그래프 탐색
        // then
        System.out.println("findTeam =" + findTeam.getTeamName());
    }

    @Test
    public void querydsl() throws Exception {
        // given

        JPAQuery query = new JPAQuery(em);
        Member member = new Member();

        List<Member> members = query.from(Member)
                .where(member.na)
        // when

        // then
    }
}
