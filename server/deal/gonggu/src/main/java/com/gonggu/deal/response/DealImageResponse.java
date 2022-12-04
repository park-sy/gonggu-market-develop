package com.gonggu.deal.response;

import com.gonggu.deal.domain.DealImage;
import lombok.Getter;

@Getter
public class DealImageResponse {
    private final String fileName;
    private final boolean isThumbnail;

    public DealImageResponse(DealImage dealImage){
        this.fileName = dealImage.getFileName();
        this.isThumbnail = dealImage.isThumbnail();
    }
}
