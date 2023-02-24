package com.jpa.board2.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    private String username;
    private int age;


    // 연관관계 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    //@ToString.Exclude // @ToString 에서 제외, 연관관계 있는 필드에 toString 적용시 무한루프 문제
    private Team team;

    // 생성자
    public Member(String username){
        this(username, 0, null);
    }

    public Member(String username, int age){
        this(username, age, null);
    }


    // Team 과의 연관관계
    public Member(String username, int age, Team team){
        this.username = username;
        this.age = age;

        if(team != null){
            changeTeam(team);
        }
    }

    public void changeTeam(Team team){
        this.team =team;
        team.getMembers().add(this);
    }
}
