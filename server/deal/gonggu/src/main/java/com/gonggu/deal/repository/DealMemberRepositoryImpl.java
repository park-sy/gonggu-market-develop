package com.gonggu.deal.repository;

import com.gonggu.deal.domain.DealMember;
import com.gonggu.deal.domain.QDealImage;
import com.gonggu.deal.domain.QDealMember;
import com.gonggu.deal.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import static com.gonggu.deal.domain.QDeal.deal;
import static com.gonggu.deal.domain.QDealImage.dealImage;
import static com.gonggu.deal.domain.QDealMember.dealMember;

@RequiredArgsConstructor //자동으로 생성자 주입
@ToString
public class DealMemberRepositoryImpl implements DealMemberRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<DealMember> getByUser(User user, Boolean host){
        return jpaQueryFactory.selectFrom(dealMember)
                .where(dealMember.user.eq(user),
                        dealMember.host.eq(host))
                .fetch();
    }
}
