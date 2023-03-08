package com.jpa.board2.controller;

import com.jpa.board2.entity.Member;
import com.jpa.board2.entity.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Profile("local") // profile 이 'local' 인 경우에만 동작
@Component
@RequiredArgsConstructor
public class InitMember {

    private final InitMemberService initMemberService;

    // 실행
    @PostConstruct // 스프링 라이프 cycle 때문에 @transactional 사용 불가 --> 따로 분리
    public void init(){
        initMemberService.init();
    }

    // 더미 데이터 생성
    @Component
    static class InitMemberService {

        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void init(){
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");

            em.persist(teamA);
            em.persist(teamB);

            for (int i = 0; i < 100; i++){
                Team selectedTeam = i % 2 == 0 ? teamA : teamB;
                em.persist(new Member("member" + i, i, selectedTeam));
            }
        }
    }
}
