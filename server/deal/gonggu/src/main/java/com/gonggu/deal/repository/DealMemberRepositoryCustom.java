package com.gonggu.deal.repository;

import com.gonggu.deal.domain.DealMember;
import com.gonggu.deal.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface DealMemberRepositoryCustom {

    List<DealMember> getByUser(User user,Boolean host);
}
