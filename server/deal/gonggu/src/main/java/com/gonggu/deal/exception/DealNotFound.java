package com.gonggu.deal.exception;

public class DealNotFound extends DealException{

    private static final String message = "게시글을 찾을 수 없습니다.";
    public DealNotFound(){
        super(message);
    }

    @Override
    public int getStatusCode(){
        return 404;
    }
}
