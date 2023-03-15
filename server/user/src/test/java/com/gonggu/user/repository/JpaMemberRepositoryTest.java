package com.gonggu.user.repository;

import com.gonggu.user.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class JpaMemberRepositoryTest {

    @Autowired
    JpaMemberRepository memberRepository;

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

        String nickname2 = "member2";
        String email2 = "test2@gmail.com";
        String password2 = "1234567";

        // when
        Member a = memberRepository.save(Member.builder()
                .nickname(nickname1)
                .email(email1)
                .password(password1)
                .build());

        List<Member> alist = memberRepository.findAll();
        System.out.println(alist.size());
        System.out.println("✅ First Save: " + a.getNickname() + " " + a.getEmail() + " " + a.getPassword());
        //System.out.println(memberRepository);

        Member b = memberRepository.save(Member.builder()
                .nickname(nickname2)
                .email(email2)
                .password(password2)
                .build());

        List<Member> blist = memberRepository.findAll();
        System.out.println(blist.size());
        System.out.println("✅ Second Save: " + b.getNickname() + " " + b.getEmail() + " " + b.getPassword());
        //System.out.println(memberRepository);

        /*
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberRepository.save(Member.builder()
                .nickname(nickname1)
                .email(email2)
                .password(password2)
                .build()));
        */
    }
}