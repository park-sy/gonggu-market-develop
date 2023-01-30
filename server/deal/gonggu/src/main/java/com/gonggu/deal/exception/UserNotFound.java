package com.gonggu.deal.exception;

public class UserNotFound extends DealException{
    private static final String message = "사용자를 찾을 수 없습니다.";
    public UserNotFound(){
        super(message);
    }

    @Override
    public int getStatusCode(){
        return 404;
    }
}
