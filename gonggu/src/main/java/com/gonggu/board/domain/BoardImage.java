package com.gonggu.board.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class BoardImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String originFileName;
    private String newFileName;
    private String filePath;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.MERGE)
    @JoinColumn(name = "board_id")
    private Board board;
}
