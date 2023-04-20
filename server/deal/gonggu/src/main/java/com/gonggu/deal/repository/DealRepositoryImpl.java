package com.gonggu.deal.repository;

import com.gonggu.deal.batch.DealForExpires;
import com.gonggu.deal.domain.*;
import com.gonggu.deal.request.DealSearch;
import com.gonggu.deal.response.DealImageResponse;
import com.gonggu.deal.response.DealResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.gonggu.deal.domain.QDeal.deal;
import static com.gonggu.deal.domain.QDealImage.dealImage;
import static com.gonggu.deal.domain.QDealMember.dealMember;

@RequiredArgsConstructor //자동으로 생성자 주입
@ToString
public class DealRepositoryImpl implements DealRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<Deal> getList(DealSearch dealSearch, User user){

        return jpaQueryFactory.selectFrom(deal)
                .where(
                        goePrice(dealSearch.getMinPrice()),
                        loePrice(dealSearch.getMaxPrice()),
                        containsTitle(dealSearch.getTitle()),
                        eqCategory(dealSearch.getCategory())
                        //loeDistance(user) // 이번에는 미사용
                )
                .limit(dealSearch.getSize())
                .offset(dealSearch.getOffset())
                .orderBy(sortOrder(dealSearch.getOrder()))
                .fetch();
    }
    @Override
    public List<DealResponse> getList2(DealSearch dealSearch, User user){
        List<DealResponse> dealResponseList =  jpaQueryFactory
                .select(Projections.constructor(DealResponse.class
                    , deal.id.as("id")
                    , deal.title.as("title")
                    , deal.unitPrice.as("unitPrice")
                    , deal.quantity.as("quantity")
                    , deal.nowCount.as("nowCount")
                    , deal.totalCount.as("totalCount")
                    , deal.deletion.as("deleted")
                    , deal.expireTime.as("expiredDate")
                    , deal.category.id.as("categoryId")
                    )
                )
                .from(deal)
                .where(
                        goePrice(dealSearch.getMinPrice()),
                        loePrice(dealSearch.getMaxPrice()),
                        containsTitle(dealSearch.getTitle()),
                        //containsContent(dealSearch.getSearchKey()),
                        eqCategory(dealSearch.getCategory())
                        //loeDistance(user)
                )
                .limit(dealSearch.getSize())
                .offset(dealSearch.getOffset())
                .orderBy(sortOrder(dealSearch.getOrder()))
                .fetch();
        dealImageBatch(dealResponseList);
        categoryBatch(dealResponseList);
        return dealResponseList;
    }
    @Override
    public List<DealResponse> getList3(DealSearch dealSearch, User user){

        List<Long> dealIds = jpaQueryFactory
                .select(deal.id)
                .from(deal)
                .where(
                        goePrice(dealSearch.getMinPrice()),
                        loePrice(dealSearch.getMaxPrice()),
                        containsTitle(dealSearch.getTitle()),
                        //containsContent(dealSearch.getSearchKey()),
                        eqCategory(dealSearch.getCategory())
                        //loeDistance(user)
                )
                .orderBy(sortOrder(dealSearch.getOrder()))
                .limit(dealSearch.getSize())
                .offset(dealSearch.getOffset())
                .fetch();

        List<DealResponse> dealResponseList =  jpaQueryFactory
                .select(Projections.constructor(DealResponse.class
                                , deal.id.as("id")
                                , deal.title.as("title")
                                , deal.unitPrice.as("unitPrice")
                                , deal.quantity.as("quantity")
                                , deal.nowCount.as("nowCount")
                                , deal.totalCount.as("totalCount")
                                , deal.deletion.as("deleted")
                                , deal.expireTime.as("expiredDate")
                                , deal.category.id.as("categoryId")
                        )
                )
                .from(deal)
                .where(
                        deal.id.in(dealIds)
                )
                .limit(dealSearch.getSize())
                .offset(dealSearch.getOffset())
                .fetch();
        dealImageBatch(dealResponseList);
        categoryBatch(dealResponseList);
        return dealResponseList;
    }
    private void dealImageBatch(List<DealResponse> dealResponseList){
        List<Long> dealIds = dealResponseList.stream()
                .map(DealResponse::getId).collect(Collectors.toList());

        List<DealImageResponse> dealImages = jpaQueryFactory
                .select(Projections.constructor(DealImageResponse.class
                        , dealImage.deal.id.as("dealId")
                        , dealImage.fileName
                        , dealImage.thumbnail.as("isThumbnail")
                        ))
                .from(dealImage)
                .where(
                        dealImage.deal.id.in(dealIds)
                        , dealImage.thumbnail.eq(true)
                        )
                .fetch();
        Map<Long, List<DealImageResponse>> map = dealImages.stream()
                .collect(Collectors.groupingBy(DealImageResponse::getDealId));
        dealResponseList.forEach(dto -> dto.setImage(map.get(dto.getId()).get(0)));
    }
    private void categoryBatch(List<DealResponse> dealResponseList){
        List<Long> categoryId = dealResponseList.stream()
                .map(DealResponse::getCategoryId).collect(Collectors.toList());

        List<Category> categories = jpaQueryFactory
                .selectFrom(QCategory.category)
                .where(QCategory.category.id.in(categoryId))
                .fetch();
        Map<Long, List<Category>> map = categories.stream()
                .collect(Collectors.groupingBy(Category::getId));
        dealResponseList.forEach(dto -> dto.setCategory(map.get(dto.getCategoryId()).get(0).getName()));
    }

    private OrderSpecifier<?> sortOrder(Integer order){
        if(order == null) {
            return deal.id.desc();
        } else if(order == 1) {
            return deal.view.desc();
        } else if(order == 2) {
            return deal.totalCount.subtract(deal.nowCount).asc();
        }
        return deal.id.desc();
    }
    private BooleanExpression goePrice(Integer minPrice){
        if(minPrice == null) {
            return null;
        }
        return deal.price.goe(minPrice);
    }
    private BooleanExpression loePrice(Integer maxPrice){
        if(maxPrice == null) {
            return null;
        }
        return deal.price.loe(maxPrice);
    }
    private BooleanExpression containsTitle(String name){
        if(name == null) {
            return null;
        }
        return deal.title.contains(name);
    }
    private BooleanExpression containsContent(String name){
        if(name == null) {
            return null;
        }
        return deal.content.contains(name);
    }
    private BooleanExpression eqCategory(String categoryName){
        if(categoryName == null) {
            return null;
        }
        Category category = jpaQueryFactory.selectFrom(QCategory.category)
                .where(QCategory.category.name.eq(categoryName))
                .fetchOne();
        return deal.category.eq(category);
    }

    private BooleanExpression loeDistance(User user){
        if(user == null) {
            return null;
        }
        return Expressions.stringTemplate("ST_DISTANCE_SPHERE({0},{1})",user.getPoint(), deal.point)
                .loe(String.valueOf(user.getDistance()));
    }
    @Override
    public void updateView(Long id){
        jpaQueryFactory.update(deal)
                .set(deal.view, deal.view.add(1))
                .where(deal.id.eq(id))
                .execute();
    }

    @Override
    public void deleteDeal(Long id){
        jpaQueryFactory.update(deal)
                .set(deal.deletion, false)
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

    @Override
    public List<DealForExpires> getDealIdByDate(LocalDate targetDate){
        return jpaQueryFactory
                .select(Projections.constructor
                        (DealForExpires.class
                        ,deal.id
                        ,deal.title))
                .from(deal)
                .where(
                        deal.expireTime.between(targetDate.atTime(0,0,0), targetDate.atTime(23,59,59))
                        ,deal.deletion.eq(false))
                .fetch();
    }
}
