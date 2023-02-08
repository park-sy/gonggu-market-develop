package com.gonggu.deal.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DealMemberToPush {
    private Long dealId;
    private String title;
    private List<String> nickname;
    private Integer timeRemaining;
    public DealMemberToPush(Long dealId, String title, List<String> nickname) {
        this.dealId = dealId;
        this.title = title;
        this.nickname = nickname;
    }
}
