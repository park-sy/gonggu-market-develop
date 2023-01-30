package com.gonggu.pay.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "nickname")
    private User user;

    private Long balance;

    public void plusBalance(Long amount){
        this.balance += amount;
    }

    public void minusBalance(Long amount){
        this.balance -= amount;
    }
}
