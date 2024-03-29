package com.gonggu.user.repository;

import com.gonggu.user.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByEmail(String email);
    List<Member> findAll();
}
