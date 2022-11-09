package com.gonggu.board.response;

import com.gonggu.board.domain.BoardImage;
import lombok.Getter;

@Getter
public class BoardImageResponse {
    private final String path;
    private final String local;

    public BoardImageResponse(BoardImage boardImage){
        this.local = "";
        this.path = boardImage.getFilePath();
    }
}
