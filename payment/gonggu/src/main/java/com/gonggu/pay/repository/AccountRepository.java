package com.gonggu.pay.repository;

import com.gonggu.pay.domain.Account;
import com.gonggu.pay.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUser(User user);
}
