package com.gonggu.deal.repository;

import com.gonggu.deal.domain.Deal;
import com.gonggu.deal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealRepository extends JpaRepository<Deal, Long>, DealRepositoryCustom{
    List<Deal> findByUser(User user);
}
