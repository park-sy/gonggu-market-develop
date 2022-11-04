package com.gonggu.board.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class BoardMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.MERGE)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;
    private Integer quantity;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean host;

    public void editQuantity(Integer quantity){
        this.quantity = quantity;
    }
}
