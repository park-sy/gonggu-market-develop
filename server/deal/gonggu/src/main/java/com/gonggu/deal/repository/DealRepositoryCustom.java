package com.gonggu.deal.repository;

import com.gonggu.deal.domain.Deal;
import com.gonggu.deal.domain.User;
import com.gonggu.deal.request.DealSearch;

import java.util.List;

public interface DealRepositoryCustom {

    List<Deal> getList(DealSearch dealSearch);
    void updateView(Long id);
    void deleteDeal(Long id);
    List<Deal> getJoinList(User user);
}
