package com.gonggu.deal.request;

import lombok.*;

import java.util.List;

@Getter
@Builder
public class DealEdit {

    private String content;
    private List<String> keywords;
    private List<String> images;

}
