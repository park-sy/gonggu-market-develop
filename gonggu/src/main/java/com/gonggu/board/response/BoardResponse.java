package com.gonggu.board.response;

import com.gonggu.board.domain.Board;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BoardResponse {

    private final Long id;
    private final String category;
    private final String title;
    private final Long remainDate;
    private final Long unitPrice;
    private final Integer quantity;
    private final Integer nowCount;
    private final Integer totalCount;
    private final List<BoardImageResponse> images;
    private final boolean deletion;
    public BoardResponse(Board board){
        LocalDateTime now = LocalDateTime.now();
        this.id = board.getId();
        this.category = board.getCategory().getName();
        this.title = board.getTitle();
        this.remainDate = ChronoUnit.DAYS.between(now,board.getExpireTime());
        this.unitPrice = board.getUnitPrice();
        this.quantity = board.getQuantity();
        this.nowCount = board.getNowCount();
        this.totalCount = board.getTotalCount();
        this.images = board.getImages().stream().map(BoardImageResponse::new).collect(Collectors.toList());
        this.deletion = board.isDeletion();

    }
}
