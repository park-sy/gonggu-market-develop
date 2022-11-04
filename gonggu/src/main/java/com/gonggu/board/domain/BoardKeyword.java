package com.gonggu.board.domain;

import lombok.*;

import javax.persistence.*;
@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class BoardKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String keyword;
    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.MERGE)
    @JoinColumn(name = "board_id")
    private Board board;
}
