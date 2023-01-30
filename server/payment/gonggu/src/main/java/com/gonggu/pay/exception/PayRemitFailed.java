package com.gonggu.pay.exception;

public class PayRemitFailed extends PayException{

    private static final String message = "송금에 실패하였습니다.";
    public PayRemitFailed(){
        super(message);
    }

    @Override
    public int getStatusCode(){
        return 415;
    }

    public PayRemitFailed(String message){
        super(message);
    }
}
