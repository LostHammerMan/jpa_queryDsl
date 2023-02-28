package com.jpa.board2.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

// dto 사용 이유 : 엔티티를 조회하면 Member 에 있는 모든 필드를 불러오나
//                 dto 를 사용하면 필요한 필드만 선택해 데이터를 호출 가능
@Data // setter, getter, requiredArgsConstructor ....
@NoArgsConstructor // 기본생성자 생성
public class MemberDto {

    private String username;
    private int age;

    // 생성자 + @QueryProjection
    @QueryProjection
    public MemberDto(String username, int age){
        this.username = username;
        this.age = age;
    }
}
