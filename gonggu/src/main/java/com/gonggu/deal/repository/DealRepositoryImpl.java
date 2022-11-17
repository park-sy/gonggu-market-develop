package com.gonggu.deal.repository;

import com.gonggu.deal.domain.Deal;
import com.gonggu.deal.domain.User;
import com.gonggu.deal.request.DealSearch;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gonggu.deal.domain.QDeal.deal;
import static com.gonggu.deal.domain.QDealMember.dealMember;

@RequiredArgsConstructor //자동으로 생성자 주입
@ToString
public class DealRepositoryImpl implements DealRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Deal> getList(DealSearch dealSearch){
        return jpaQueryFactory.selectFrom(deal)
                .where(
                        goePrice(dealSearch.getMinPrice()),
                        loePrice(dealSearch.getMaxPrice()),
                        containsTitle(dealSearch.getSearchKey()),
                        containsContent(dealSearch.getSearchKey()),
                        eqCategory(dealSearch.getCategory())
                        //카테고리
                )
                .limit(dealSearch.getSize())
                .offset(dealSearch.getOffset())
                .orderBy(sortOrder(dealSearch.getOrder()))
                .fetch();
    }

    private OrderSpecifier<?> sortOrder(Integer order){
        if(order == null) return deal.id.desc();
        else if(order == 1) return deal.view.desc();
        else if(order == 2) return deal.totalCount.subtract(deal.nowCount).desc();
        return deal.id.desc();
    }
    private BooleanExpression goePrice(Integer minPrice){
        if(minPrice == null) return null;
        return deal.price.goe(minPrice);
    }
    private BooleanExpression loePrice(Integer maxPrice){
        if(maxPrice == null) return null;
        return deal.price.loe(maxPrice);
    }
//    private BooleanExpression eqCategory(String category){
//        if(category == null) return null;
//        return deal.cate
//    }
    private BooleanExpression containsTitle(String name){
        if(name == null) return null;
        return deal.title.contains(name);
    }
    private BooleanExpression containsContent(String name){
        if(name == null) return null;
        return deal.content.contains(name);
    }
    private BooleanExpression eqCategory(String category){
        if(category == null) return null;
        return deal.category.name.eq(category);
    }

    @Override
    @Transactional
    public void updateView(Long id){
        jpaQueryFactory.update(deal)
                .set(deal.view, deal.view.add(1))
                .where(deal.id.eq(id))
                .execute();
    }

    @Override
    public void deleteDeal(Long id){
        jpaQueryFactory.update(deal)
                .set(deal.deletion, deal.deletion)
                .where(deal.id.eq(id))
                .execute();
    }

    @Override
    public List<Deal> getJoinList(User user){
        return jpaQueryFactory.selectFrom(deal)
                .innerJoin(dealMember).on(deal.eq(dealMember.deal))
                .where(
                        dealMember.user.eq(user),
                        dealMember.host.eq(false)
                )
                .orderBy(deal.createTime.desc())
                .fetch();
    }

}
