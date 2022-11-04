package com.gonggu.board.repository;

import com.gonggu.board.domain.BoardKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardKeywordRepository extends JpaRepository<BoardKeyword, Long> {
}
