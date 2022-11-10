package com.gonggu.board.response;

import com.gonggu.board.domain.Board;
import com.gonggu.board.domain.BoardKeyword;
import com.gonggu.board.domain.Category;
import com.gonggu.board.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
@Getter
public class BoardDetailResponse {

    private final Long id;
    private final String title;
    //삭제
    private final String content;
    //단위 가격으로
    private final Long price;
    private final Long unitPrice;
    private final Long remainDate;
    private final Integer quantity;
    private final Integer unitQuantity;
    private final String unit;
    private final Integer nowCount;
    private final Integer totalCount;
    private final String url;
    private final List<BoardImageResponse> images;
    private final int view;
    private final boolean deletion;
    private final UserResponse user;
    private final Category category;
    public BoardDetailResponse(Board board){
        LocalDateTime now = LocalDateTime.now();
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.remainDate = ChronoUnit.DAYS.between(now,board.getExpireTime());
        this.price = board.getPrice();
        this.unitPrice = board.getUnitPrice();
        this.quantity = board.getQuantity();
        this.unitQuantity = board.getUnitQuantity();
        this.unit = board.getUnit();
        this.nowCount = board.getNowCount();
        this.totalCount = board.getTotalCount();
        this.url = board.getUrl();
        this.view = board.getView();
        this.images = board.getImages().stream().map(BoardImageResponse::new).collect(Collectors.toList());
        this.deletion = board.isDeletion();
        this.user = new UserResponse(board.getUser());
        this.category = board.getCategory();
    }
}
