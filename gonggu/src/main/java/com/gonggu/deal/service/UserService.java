package com.gonggu.deal.service;

import com.gonggu.deal.domain.User;
import com.gonggu.deal.repository.UserRepository;
import com.gonggu.deal.request.UserCreate;
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
