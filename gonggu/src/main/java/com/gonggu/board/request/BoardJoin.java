package com.gonggu.board.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;

@Setter
@Getter
@ToString
@Builder
public class BoardJoin {

    private Integer quantity;

    @JsonCreator
    public BoardJoin(Integer quantity){
        this.quantity = quantity;
    }
}
