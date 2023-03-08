package com.gonggu.user.service;

import com.gonggu.user.domain.Member;
import com.gonggu.user.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public String join(Member member) {

        memberRepository.findByNickname(member.getNickname()).ifPresent(m -> {
            throw new IllegalStateException("❗️ Nickname already exists.");
        });

        memberRepository.findByEmail(member.getEmail()).ifPresent(m -> {
            throw new IllegalStateException("❗️ Email already exists.");
        });

        memberRepository.save(member);
        return member.getNickname();
    }
}
