package com.gonggu.deal.response;

import com.gonggu.deal.domain.DealMember;
import lombok.Getter;

@Getter
public class DealMemberResponse {
    private String userId;
    private String name;

    public DealMemberResponse(DealMember dealMember) {
        this.userId = dealMember.getUser().getNickname();
        this.name = dealMember.getUser().getNickname();
    }
}
