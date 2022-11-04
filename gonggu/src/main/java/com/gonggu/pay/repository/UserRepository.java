package com.gonggu.pay.repository;

import com.gonggu.pay.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
