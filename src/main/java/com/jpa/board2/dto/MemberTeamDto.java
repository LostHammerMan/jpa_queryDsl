package com.jpa.board2.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

// member, team 의 정보를 섞어 필요한 정보만을 가져올 DTO
@Data
public class MemberTeamDto {

    private Long memberId;
    private String username;
    private int age;
    private Long teamId;
    private String teamName;

    // @QueryProjection : 생성자를 통해 DTO를 조회하는 방법과 함께 사용, 단점 : 의존성 문제
    @QueryProjection
    public MemberTeamDto(Long memberId, String username, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
