package com.gonggu.deal.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gonggu.deal.domain.Category;
import com.gonggu.deal.domain.Deal;
import com.gonggu.deal.domain.DealImage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DealResponse {

    private final Long id;
    private final String title;
    private final Long remainDate;
    private final Long unitPrice;
    private final Integer quantity;
    private final Integer nowCount;
    private final Integer totalCount;
    private final boolean deleted;
    private final boolean expired;
    private String category;
    private DealImageResponse image;
    private Integer userCount;
    private String unit;
    private LocalDateTime expiredDate;
    private String hostName;
    private Long categoryId;

    public void setCategory(String category) {
        this.category = category;
    }
    public void setImage(DealImageResponse dealImageResponse){
        this.image = dealImageResponse;
    }
    public DealResponse(Long id, String title, Long unitPrice, Integer quantity, Integer nowCount,
                        Integer totalCount, Boolean deleted, LocalDateTime expiredDate, Long categoryId) {
        LocalDateTime now = LocalDateTime.now().plusHours(9);
        this.id = id;
        this.title = title;
        this.remainDate = ChronoUnit.DAYS
                .between(now.toLocalDate(),expiredDate.toLocalDate());
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.nowCount = nowCount;
        this.totalCount = totalCount;
        this.deleted = deleted;
        this.expired = expiredDate.toLocalDate().isBefore(now.toLocalDate());
        this.categoryId = categoryId;
    }

    public DealResponse(Deal deal){
        LocalDateTime now = LocalDateTime.now().plusHours(9);
        this.id = deal.getId();
        this.category = deal.getCategory().getName();
        this.title = deal.getTitle();
        this.remainDate = ChronoUnit.DAYS
                .between(now.toLocalDate(),deal.getExpireTime().toLocalDate());
        this.unitPrice = deal.getUnitPrice();
        this.quantity = deal.getQuantity();
        this.nowCount = deal.getNowCount();
        this.totalCount = deal.getTotalCount();
        this.image = new DealImageResponse(deal.getImages().get(0));
        this.deleted = deal.isDeletion();
        this.expired = deal.getExpireTime().toLocalDate().isBefore(now.toLocalDate());
    }

    public DealResponse(Deal deal, Integer userCount){
        LocalDateTime now = LocalDateTime.now().plusHours(9);
        this.id = deal.getId();
        this.category = deal.getCategory().getName();
        this.title = deal.getTitle();
        this.remainDate = ChronoUnit.DAYS
                .between(now.toLocalDate(),deal.getExpireTime().toLocalDate());
        this.unitPrice = deal.getUnitPrice();
        this.quantity = deal.getQuantity();
        this.nowCount = deal.getNowCount();
        this.totalCount = deal.getTotalCount();
        this.image = new DealImageResponse(deal.getImages().get(0));
        this.deleted = deal.isDeletion();
        this.expired = deal.getExpireTime().toLocalDate().isBefore(now.toLocalDate());
        this.userCount = userCount;
        this.unit = deal.getUnit();
        this.expiredDate = deal.getExpireTime();
        this.hostName = deal.getUser().getNickname();
    }
}
