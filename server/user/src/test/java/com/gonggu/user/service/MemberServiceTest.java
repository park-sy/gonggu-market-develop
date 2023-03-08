package com.gonggu.user.service;

import com.gonggu.user.domain.Member;
import com.gonggu.user.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void joinTest() {
        // Given
        Member member = Member.builder()
                .nickname("nick1")
                .email("nick1@gmail.com")
                .password("1234")
                .build();

        String saveNickname = memberService.join(member);

        Member findMember = memberRepository.findByNickname(saveNickname).get();
        assertEquals(member.getNickname(), findMember.getNickname());
    }

    @Test
    public void duplicateNicknameJoinTest() {

        Member member1 = Member.builder()
                .nickname("nick1")
                .email("nick1@gmail.com")
                .password("1234")
                .build();

        Member member2 = Member.builder()
                .nickname("nick1")
                .email("nick2@gmail.com")
                .password("1234")
                .build();

        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
            memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("❗️ Nickname already exists.");
    }

    @Test
    public void duplicateEmailJoinTest() {

        Member member1 = Member.builder()
                .nickname("nick1")
                .email("same@gmail.com")
                .password("1234")
                .build();

        Member member2 = Member.builder()
                .nickname("nick12")
                .email("same@gmail.com")
                .password("1234")
                .build();

        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
                memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("❗️ Email already exists.");
    }
}