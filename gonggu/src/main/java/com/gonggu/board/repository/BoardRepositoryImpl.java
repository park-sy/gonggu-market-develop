package com.gonggu.board.repository;

import com.gonggu.board.domain.Board;
import com.gonggu.board.domain.QBoard;
import com.gonggu.board.domain.QBoardMember;
import com.gonggu.board.domain.User;
import com.gonggu.board.request.BoardSearch;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gonggu.board.domain.QBoard.board;
import static com.gonggu.board.domain.QBoardMember.boardMember;

@RequiredArgsConstructor //자동으로 생성자 주입
@ToString
public class BoardRepositoryImpl implements BoardRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Board> getList(BoardSearch boardSearch){
        return jpaQueryFactory.selectFrom(board)
                .where(
                        goePrice(boardSearch.getMinPrice()),
                        loePrice(boardSearch.getMaxPrice()),
                        containsTitle(boardSearch.getSearchKey()),
                        containsContent(boardSearch.getSearchKey()),
                        eqCategory(boardSearch.getCategory())
                        //카테고리
                )
                .limit(boardSearch.getSize())
                .offset(boardSearch.getOffset())
                .orderBy(sortOrder(boardSearch.getOrder()))
                .fetch();
    }

    private OrderSpecifier<?> sortOrder(Integer order){
        if(order == null) return board.id.desc();
        else if(order == 1) return board.view.desc();
        else if(order == 2) return board.totalCount.subtract(board.nowCount).desc();
        return board.id.desc();
    }
    private BooleanExpression goePrice(Integer minPrice){
        if(minPrice == null) return null;
        return board.price.goe(minPrice);
    }
    private BooleanExpression loePrice(Integer maxPrice){
        if(maxPrice == null) return null;
        return board.price.loe(maxPrice);
    }
//    private BooleanExpression eqCategory(String category){
//        if(category == null) return null;
//        return board.cate
//    }
    private BooleanExpression containsTitle(String name){
        if(name == null) return null;
        return board.title.contains(name);
    }
    private BooleanExpression containsContent(String name){
        if(name == null) return null;
        return board.content.contains(name);
    }
    private BooleanExpression eqCategory(String category){
        if(category == null) return null;
        return board.category.name.eq(category);
    }

    @Override
    @Transactional
    public void updateView(Long id){
        jpaQueryFactory.update(board)
                .set(board.view, board.view.add(1))
                .where(board.id.eq(id))
                .execute();
    }

    @Override
    public void deleteBoard(Long id){
        jpaQueryFactory.update(board)
                .set(board.deletion, board.deletion)
                .where(board.id.eq(id))
                .execute();
    }

    @Override
    public List<Board> getJoinList(User user){
        return jpaQueryFactory.selectFrom(board)
                .innerJoin(boardMember).on(board.eq(boardMember.board))
                .where(
                        boardMember.user.eq(user),
                        boardMember.host.eq(false)
                )
                .orderBy(board.createTime.desc())
                .fetch();
    }
}
