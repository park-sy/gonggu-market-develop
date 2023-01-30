package com.gonggu.deal.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DealMemberToPush {
    private Long dealId;
    private String title;
    private List<String> nickname;
}
