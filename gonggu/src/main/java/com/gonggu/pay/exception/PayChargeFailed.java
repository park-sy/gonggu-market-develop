package com.gonggu.pay.exception;

public class PayChargeFailed extends PayException{
    private static final String message = "조인에 실패하였습니다.";
    public PayChargeFailed(){
        super(message);
    }

    @Override
    public int getStatusCode(){
        return 404;
    }

    public PayChargeFailed(String message){
        super(message);
    }
}
