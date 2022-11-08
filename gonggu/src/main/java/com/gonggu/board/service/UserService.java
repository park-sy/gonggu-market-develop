package com.gonggu.board.service;

import com.gonggu.board.domain.User;
import com.gonggu.board.repository.UserRepository;
import com.gonggu.board.request.UserCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    public final UserRepository userRepository;

    public void createUser(UserCreate userCreate) {
        User user = User.builder()
                .id(userCreate.getId())
                .name(userCreate.getName())
                .build();
        userRepository.save(user);
    }

}
