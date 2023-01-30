package com.gonggu.deal.exception;

public class CategoryNotFound extends DealException{
    private static final String message = "카테고리를 찾을 수 없습니다.";
    public CategoryNotFound(){
        super(message);
    }

    @Override
    public int getStatusCode(){
        return 404;
    }

    public CategoryNotFound(String message){
        super(message);
    }
}
