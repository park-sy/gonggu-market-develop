package com.gonggu.deal.request;

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
public class DealCreate {

    private String title;
    private String content;
    private Long price;
    private Integer unitQuantity;
    private String unit;
    private Integer nowCount;
    private Integer totalCount;
    private String url;
    private Long categoryId;
    private List<String> keywords;
    private LocalDateTime expireTime;

    //썸네일
    //이미지
    //거래 성사율
    //

}
