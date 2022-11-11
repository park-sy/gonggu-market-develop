package com.gonggu.board.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Builder
public class BoardEdit {

    private String content;
    @JsonCreator
    public BoardEdit(String content){
        this.content = content;
    }
}
