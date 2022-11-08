package com.gonggu.board.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
@Builder
public class BoardCreate {

    private Long id;
    private String title;
    private String content;
    private Long price;
    private Integer quantity;
    private Integer nowCount;
    private String url;
    private Integer recruitmentNumber;
    private List<String> keywords;
}
