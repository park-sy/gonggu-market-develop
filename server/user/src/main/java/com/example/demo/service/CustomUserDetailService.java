package com.example.demo.service;

import com.example.demo.entity.Keyword;
import com.example.demo.entity.User;
import com.example.demo.entity.UserKeyword;
import com.example.demo.repository.KeywordRepository;
import com.example.demo.repository.UserKeywordRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService{
    private final UserRepository userRepository;
    //private final JwtTokenProvider jwtTokenProvider;
    private final KeywordRepository keywordRepository;
    private final UserKeywordRepository userKeywordRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public void updateKeywords(String username, List<String> keywords){

        Optional<User> user = userRepository.findByNickname(username);
        userKeywordRepository.deleteAll(userKeywordRepository.findByUser(user.get()));
        for(String k : keywords){

            Optional<Keyword> kk = keywordRepository.findByWord(k);
            if(kk.isEmpty()) {
                keywordRepository.save(Keyword.builder()
                        .word(k)
                        .build());
                kk = keywordRepository.findByWord(k);
            }
            userKeywordRepository.save(UserKeyword.builder()
                    .keyword(kk.get())
                    .user(user.get())
                    .build());
        }
    }

    public String getKeyword(User user){
        List<String> keylist = new ArrayList<>();
        String str = "[\"";
        List<UserKeyword> list = userKeywordRepository.findByUser(user);

        if(list.isEmpty())  return "[]";
        for(UserKeyword userKeyword : list){
            str = str+ userKeyword.getKeyword().getWord();
            str = str+"\", \"";
            keylist.add(userKeyword.getKeyword().getWord());
        }
        str = str.substring(0, str.length()-3);
        str = str+"]";
        return str;
    }
    public void updateEmail(User user, String str){
        //if(user == null) throw new NotFoundException("changePassword(),멤버가 조회되지 않음");
        //user.changePassword(pwd);
        user.setEmail(str);
        userRepository.save(user);
    }
    public void updatePassword(User user, String str){
        //if(user == null) throw new NotFoundException("changePassword(),멤버가 조회되지 않음");
        //user.changePassword(pwd);
        user.setPassword(str);
        userRepository.save(user);
    }

}
