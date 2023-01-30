package com.gonggu.deal.repository;

import com.gonggu.deal.domain.Deal;
import com.gonggu.deal.domain.DealImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealImageRepository extends JpaRepository<DealImage, Long> {
    List<DealImage> findByDeal(Deal deal);
}
