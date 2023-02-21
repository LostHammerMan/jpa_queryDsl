package com.jpa.board2.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MEMBERS")
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long memberId;

    private String name;

    private String city;
    private String street;
    private String zipcode;

 /*   @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;*/

    // ORDER와 1:N 관계
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

}
