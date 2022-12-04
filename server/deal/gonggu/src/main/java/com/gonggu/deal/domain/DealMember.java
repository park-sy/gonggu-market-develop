package com.gonggu.deal.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class DealMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deal_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.MERGE)
    @JoinColumn(name = "deal_id")
    private Deal deal;

    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.MERGE)
    @JoinColumn(name = "nickname")
    private User user;
    private Integer quantity;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean host;

    public void editQuantity(Integer quantity){
        this.quantity = quantity;
    }
}
