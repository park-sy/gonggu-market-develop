package com.gonggu.board.repository;

import com.gonggu.board.domain.Board;
import com.gonggu.board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom{
    List<Board> findByUser(User user);
}
