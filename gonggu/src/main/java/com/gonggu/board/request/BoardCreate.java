package com.gonggu.board.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
public class BoardCreate {

    private String title;
    private String content;
    private Long price;
    private Integer quantity;
    private Integer unitQuantity;
    private Integer nowCount;
    private String url;
    private Long categoryId;
    private List<String> keywords;
    private LocalDateTime expireTime;

    //썸네일
    //이미지
    //거래 성사율
    //

}
