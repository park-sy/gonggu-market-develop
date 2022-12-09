package com.gonggu.pay.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
public class PaymentToPush {
    private final String nickName;
    private final Long amount;
    private final LocalDateTime remitTime;
}
