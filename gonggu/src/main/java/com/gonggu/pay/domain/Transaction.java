package com.gonggu.pay.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "nickname", name="from_id")
    private User from;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "nickname", name="to_id")
    private User to;

    private LocalDateTime date;
    private Long amount;
}
