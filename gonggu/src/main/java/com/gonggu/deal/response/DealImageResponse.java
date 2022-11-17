package com.gonggu.deal.response;

import com.gonggu.deal.domain.DealImage;
import lombok.Getter;

@Getter
public class DealImageResponse {
    private final String path;
    private final String local;

    public DealImageResponse(DealImage dealImage){
        this.local = "";
        this.path = dealImage.getFilePath();
    }
}
