package com.gonggu.deal.repository;

import com.gonggu.deal.domain.Deal;
import com.gonggu.deal.domain.DealKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealKeywordRepository extends JpaRepository<DealKeyword, Long> {
    List<DealKeyword> findByDeal(Deal deal);
}
