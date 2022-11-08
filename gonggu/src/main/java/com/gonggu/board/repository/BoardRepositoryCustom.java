package com.gonggu.board.repository;

import com.gonggu.board.domain.Board;
import com.gonggu.board.domain.User;
import com.gonggu.board.request.BoardSearch;

import java.util.List;

public interface BoardRepositoryCustom {

    List<Board> getList(BoardSearch boardSearch);
    void updateView(Long id);
    void deleteBoard(Long id);
    List<Board> getJoinList(User user);
}
