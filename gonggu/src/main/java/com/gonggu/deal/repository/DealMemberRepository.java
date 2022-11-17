package com.gonggu.deal.repository;

import com.gonggu.deal.domain.Deal;
import com.gonggu.deal.domain.DealMember;
import com.gonggu.deal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealMemberRepository extends JpaRepository<DealMember, Long> {
    DealMember findByDealAndUser(Deal deal, User user);
    List<DealMember> findByDeal(Deal deal);
}
