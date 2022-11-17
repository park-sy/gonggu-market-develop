package com.gonggu.deal.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserCreate {
    private Long id;
    private String name;

    //위치 추가
}
