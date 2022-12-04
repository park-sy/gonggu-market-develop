package com.gonggu.pay.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserTemp {
    private Long id;
    private String name;
}
