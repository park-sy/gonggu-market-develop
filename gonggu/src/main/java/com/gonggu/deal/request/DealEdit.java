package com.gonggu.deal.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Builder
public class DealEdit {

    private String content;
    @JsonCreator
    public DealEdit(String content){
        this.content = content;
    }
}
