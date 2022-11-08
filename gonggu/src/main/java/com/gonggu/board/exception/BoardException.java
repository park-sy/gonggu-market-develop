package com.gonggu.board.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class BoardException extends RuntimeException{
    public final Map<String, String> validation = new HashMap<>();

    public BoardException(String message) {
        super(message);
    }

    public BoardException(String message, Throwable cause) {
        super(message, cause);
    }
    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message){
        validation.put(fieldName,message);
    }
}
