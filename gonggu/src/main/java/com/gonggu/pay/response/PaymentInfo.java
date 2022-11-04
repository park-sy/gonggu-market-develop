package com.gonggu.pay.response;

import com.gonggu.pay.domain.Payment;
import lombok.Getter;

@Getter
public class PaymentInfo {
    private final Long id;
    private final Long balance;
    public PaymentInfo(Payment payment) {
        this.id = payment.getId();
        this.balance = payment.getBalance();
    }
}
