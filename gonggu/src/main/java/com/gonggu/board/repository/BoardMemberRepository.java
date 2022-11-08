package com.gonggu.board.repository;

import com.gonggu.board.domain.Board;
import com.gonggu.board.domain.BoardMember;
import com.gonggu.board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {
    BoardMember findByBoardAndUser(Board board, User user);
    List<BoardMember> findByBoard(Board board);
}
