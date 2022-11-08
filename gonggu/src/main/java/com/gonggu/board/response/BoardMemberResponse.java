package com.gonggu.board.response;

import com.gonggu.board.domain.BoardMember;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardMemberResponse {
    private Long userId;
    private String name;

    public BoardMemberResponse(BoardMember boardMember) {
        this.userId = boardMember.getUser().getId();
        this.name = boardMember.getUser().getName();
    }
}
