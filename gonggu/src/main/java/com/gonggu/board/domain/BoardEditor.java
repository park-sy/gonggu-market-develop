package com.gonggu.board.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardEditor {
    private String content;
    private boolean deletion;


    @Builder
    public BoardEditor(String content, boolean deletion){
        this.content = content;
        this.deletion = deletion;
    }
}
