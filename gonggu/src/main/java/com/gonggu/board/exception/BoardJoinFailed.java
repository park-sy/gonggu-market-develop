package com.gonggu.board.exception;

public class BoardJoinFailed extends BoardException{
    private static final String message = "조인에 실패하였습니다.";
    public BoardJoinFailed(){
        super(message);
    }

    @Override
    public int getStatusCode(){
        return 404;
    }

    public BoardJoinFailed(String message){
        super(message);
    }
}
