package com.jpa.board2.repository;

import com.jpa.board2.dto.MemberSearchCondition;
import com.jpa.board2.dto.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberTeamDto> search(MemberSearchCondition condition);

    // 단순한 쿼리
    Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);

    // 카운트 쿼리와 아닌 것을 별도의 쿼리로 처리
    Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);
}
