package com.gonggu.board.exception;

public class UserNotFound extends BoardException{
    private static final String message = "사용자를 찾을 수 없습니다.";
    public UserNotFound(){
        super(message);
    }

    @Override
    public int getStatusCode(){
        return 404;
    }
}
