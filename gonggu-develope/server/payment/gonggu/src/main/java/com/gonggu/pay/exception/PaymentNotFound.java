package com.gonggu.pay.exception;

public class PaymentNotFound extends PayException{
    private static final String message = "지갑 정보를 찾을 수 없습니다.";
    public PaymentNotFound(){
        super(message);
    }

    @Override
    public int getStatusCode(){
        return 404;
    }

    public PaymentNotFound(String message){
        super(message);
    }
}
