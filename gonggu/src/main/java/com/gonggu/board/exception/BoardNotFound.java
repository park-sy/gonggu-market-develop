package com.gonggu.board.exception;

public class BoardNotFound extends BoardException{

    private static final String message = "게시글을 찾을 수 없습니다.";
    public BoardNotFound(){
        super(message);
    }

    @Override
    public int getStatusCode(){
        return 404;
    }
}
