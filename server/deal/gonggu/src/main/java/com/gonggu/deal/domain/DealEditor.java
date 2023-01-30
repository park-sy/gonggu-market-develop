package com.gonggu.deal.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DealEditor {
    private String content;
    private boolean deletion;


    @Builder
    public DealEditor(String content, boolean deletion){
        this.content = content;
        this.deletion = deletion;
    }
}
