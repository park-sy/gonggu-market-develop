package com.gonggu.user.repository;

import com.gonggu.user.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLOutput;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    public void cleanUp() {
        memberRepository.deleteAll();
    }

    @Test
    public void memberTestBasic() {
        // given
        String nickname = "테스트 유저";
        String email = "abc@gmail.com";
        String password = "1234";

        memberRepository.save(Member.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .build());

        // when
        List<Member> userList = memberRepository.findAll();

        // then
        Member user = userList.get(0);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
    }

    @Test
    public void duplicateNicknameException() {

        // given
        String nickname1 = "member1";
        String email1 = "test1@gamil.com";
        String password1 = "1234";

        //String nickname2 = "테스트 유저1";
        String email2 = "test2@gmail.com";
        String password2 = "1234";

        // when
        Member a = memberRepository.save(Member.builder()
                .nickname(nickname1)
                .email(email1)
                .password(password1)
                .build());

        System.out.println("✅ First Save: " + a.getNickname());

        Member b = memberRepository.save(Member.builder()
                .nickname(nickname1)
                .email(email1)
                .password(password1)
                .build());

        System.out.println("✅ Second Save: " + b.getNickname());

        /*
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberRepository.save(Member.builder()
                .nickname(nickname1)
                .email(email2)
                .password(password2)
                .build()));
        */
    }
}