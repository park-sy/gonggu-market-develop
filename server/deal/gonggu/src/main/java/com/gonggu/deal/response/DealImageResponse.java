package com.gonggu.deal.response;

import com.gonggu.deal.domain.DealImage;
import lombok.Getter;

@Getter
public class DealImageResponse {


    private Long dealId;
    private final String fileName;
    private final boolean isThumbnail;

    public DealImageResponse(Long dealId, String fileName, boolean isThumbnail) {
        this.dealId = dealId;
        this.fileName = fileName;
        this.isThumbnail = isThumbnail;
    }
    public DealImageResponse(DealImage dealImage){
        this.fileName = dealImage.getFileName();
        this.isThumbnail = dealImage.isThumbnail();
    }

}
