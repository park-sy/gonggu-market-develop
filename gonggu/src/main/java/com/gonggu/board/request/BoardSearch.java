package com.gonggu.board.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Getter
@Setter
public class BoardSearch {
    private static final int MAX_SIZE = 2000;
    private Integer page;
    private Integer size;
    private String title;
    private String category;
    private Integer minPrice;
    private Integer maxPrice;

    private String searchKey;
    private Integer order;

    @Builder
    public BoardSearch(Integer page, Integer size, String title, String category,
                       Integer minPrice, Integer maxPrice, String searchKey, Integer order){
        this.page = page == null ? 1 : page;
        this.size = size == null ? 10 :size;
        this.title = title;
        this.category = category;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.searchKey = searchKey;
        this.order = order;
    }
    public long getOffset(){
        //페이지 0이 요청될 시 1페이지 반환
        return (long) (max(1,page) - 1) * min(size,MAX_SIZE);
    }
}
