package com.gonggu.pay.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentCharge {
    private Long requestCoin;
    private String account;
}
