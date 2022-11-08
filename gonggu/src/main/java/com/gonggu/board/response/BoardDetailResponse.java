package com.gonggu.board.response;

import com.gonggu.board.domain.Board;
import com.gonggu.board.domain.BoardKeyword;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
@Getter
public class BoardDetailResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final Long price;
    private final Integer quantity;
    private final Integer nowCount;
    private final String url;
    private final Integer recruitmentNumber;
    private final List<BoardImageResponse> images;
    private final List<String> keywords;
    private final int view;

    public BoardDetailResponse(Board board){
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.price = board.getPrice();
        this.quantity = board.getQuantity();
        this.nowCount = board.getNowCount();
        this.url = board.getUrl();
        this.recruitmentNumber = board.getRecruitmentNumber();
        this.view = board.getView();
        this.images = board.getImages().stream().map(BoardImageResponse::new).collect(Collectors.toList());
        this.keywords = board.getKeywords().stream().map(BoardKeyword::getKeyword).collect(Collectors.toList());

    }
}
