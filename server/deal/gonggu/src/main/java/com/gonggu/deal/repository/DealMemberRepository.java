package com.gonggu.deal.repository;

import com.gonggu.deal.domain.Deal;
import com.gonggu.deal.domain.DealMember;
import com.gonggu.deal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DealMemberRepository extends JpaRepository<DealMember, Long>, DealMemberRepositoryCustom {
    Optional<DealMember> findByDealAndUser(Deal deal, User user);
    List<DealMember> findByDeal(Deal deal);

}
