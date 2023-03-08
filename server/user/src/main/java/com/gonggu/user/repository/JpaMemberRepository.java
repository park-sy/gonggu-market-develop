package com.gonggu.user.repository;

import com.gonggu.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaMemberRepository extends JpaRepository<Member, String>, MemberRepository {

    @Override
    Member save(Member member);
    @Override
    Optional<Member> findByNickname(String nickname);
}
