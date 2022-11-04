package com.gonggu.board.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Builder
public class BoardJoin {

    private Integer quantity;
    private String name;
}
