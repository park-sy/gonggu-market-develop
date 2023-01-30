package com.gonggu.deal.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DealUserToChat {
    private final Long dealId;
    private final String title;
    private final String nickName;
}
