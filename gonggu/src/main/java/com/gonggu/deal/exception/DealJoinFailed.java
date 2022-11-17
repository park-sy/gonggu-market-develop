package com.gonggu.deal.exception;

public class DealJoinFailed extends DealException{
    private static final String message = "조인에 실패하였습니다.";
    public DealJoinFailed(){
        super(message);
    }

    @Override
    public int getStatusCode(){
        return 404;
    }

    public DealJoinFailed(String message){
        super(message);
    }
}
