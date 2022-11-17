package com.gonggu.deal.response;

import com.gonggu.deal.domain.Deal;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
public class DealResponse {

    private final Long id;
    private final String category;
    private final String title;
    private final Long remainDate;
    private final Long unitPrice;
    private final Integer quantity;
    private final Integer nowCount;
    private final Integer totalCount;
    private final DealImageResponse image;
    private final boolean deletion;
    public DealResponse(Deal deal){
        LocalDateTime now = LocalDateTime.now();
        this.id = deal.getId();
        this.category = deal.getCategory().getName();
        this.title = deal.getTitle();
        this.remainDate = ChronoUnit.DAYS.between(now,deal.getExpireTime());
        this.unitPrice = deal.getUnitPrice();
        this.quantity = deal.getQuantity();
        this.nowCount = deal.getNowCount();
        this.totalCount = deal.getTotalCount();
        this.image = new DealImageResponse(deal.getImages().get(0));
        this.deletion = deal.isDeletion();

    }
}
