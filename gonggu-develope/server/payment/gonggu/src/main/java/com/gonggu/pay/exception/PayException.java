package com.gonggu.pay.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class PayException extends RuntimeException{
    public final Map<String, String> validation = new HashMap<>();

    public PayException(String message) {
        super(message);
    }

    public PayException(String message, Throwable cause) {
        super(message, cause);
    }
    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message){
        validation.put(fieldName,message);
    }
}
