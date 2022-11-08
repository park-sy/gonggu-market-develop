package com.gonggu.board.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Builder
public class BoardEdit {

    private long boardId;
    private String content;
    private boolean deletion;

}
