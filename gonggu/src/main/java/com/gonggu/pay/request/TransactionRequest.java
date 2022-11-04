package com.gonggu.pay.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class TransactionRequest {

    private Integer order;
    private Integer to;
    private Integer from;
    private String start;
    private String end;
}
