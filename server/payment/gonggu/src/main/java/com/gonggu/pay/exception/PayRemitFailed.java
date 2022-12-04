package com.gonggu.pay.exception;

public class PayRemitFailed extends PayException{

    private static final String message = "조인에 실패하였습니다.";
    public PayRemitFailed(){
        super(message);
    }

    @Override
    public int getStatusCode(){
        return 404;
    }

    public PayRemitFailed(String message){
        super(message);
    }
}
