package com.gonggu.pay.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Account {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;
    private Long balance;

    public void plusBalance(Long amount){
        this.balance += amount;
    }

    public void minusBalance(Long amount){
        this.balance -= amount;
    }
}
