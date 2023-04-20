package com.gonggu.deal.repository;

import com.gonggu.deal.batch.DealForExpires;
import com.gonggu.deal.domain.Deal;
import com.gonggu.deal.domain.User;
import com.gonggu.deal.request.DealSearch;
import com.gonggu.deal.response.DealResponse;

import java.time.LocalDate;
import java.util.List;

public interface DealRepositoryCustom {

    List<Deal> getList(DealSearch dealSearch, User user);
    List<DealResponse> getList2(DealSearch dealSearch, User user);
    List<DealResponse> getList3(DealSearch dealSearch, User user);
    void updateView(Long id);
    void deleteDeal(Long id);
    List<Deal> getJoinList(User user);
    List<DealForExpires> getDealIdByDate(LocalDate targetDate);
}
