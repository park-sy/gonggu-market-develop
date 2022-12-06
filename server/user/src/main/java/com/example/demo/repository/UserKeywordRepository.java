package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.entity.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
    List<UserKeyword> findByUser(User user);
}
