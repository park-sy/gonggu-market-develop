package com.gonggu.pay.response;

import com.gonggu.pay.domain.Payment;
import lombok.Getter;

@Getter
public class PaymentInfo {
    private final Long walletId;
    private final Long balance;
    public PaymentInfo(Payment payment) {
        this.walletId = payment.getId();
        this.balance = payment.getBalance();
    }
}
