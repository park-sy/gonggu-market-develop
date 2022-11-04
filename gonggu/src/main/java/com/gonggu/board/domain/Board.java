package com.gonggu.board.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;
    private String title;
    private String content;
    private Long price;
    private Integer quantity;
    private String url;
    private Integer recruitmentNumber;
    private LocalDateTime createTime;
    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean deletion;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private Integer nowCount;

    @OneToMany(mappedBy ="board", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<BoardImage> images;

    @OneToMany(mappedBy ="board", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<BoardKeyword> keywords;

    @OneToMany(mappedBy ="board", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<BoardMember> members;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;

    public BoardEditor.BoardEditorBuilder toEditor(){
        return BoardEditor.builder().content(content);
    }

    public void edit(BoardEditor boardEditor){
        content = builder().content;
        deletion = builder().deletion;
    }

    public void editCount(Integer amount){
        this.nowCount = amount;
    }
}
