package com.gonggu.deal.response;

import com.gonggu.deal.domain.DealMember;
import lombok.Getter;

@Getter
public class DealMemberResponse {
    private Long userId;
    private String name;

    public DealMemberResponse(DealMember dealMember) {
        this.userId = dealMember.getUser().getId();
        this.name = dealMember.getUser().getName();
    }
}
