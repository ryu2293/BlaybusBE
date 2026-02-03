package com.blaybus.blaybusbe.domain.user.service;

import com.blaybus.blaybusbe.domain.user.dto.response.ResponseUserDto;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseUserDto findUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow();

        return ResponseUserDto.from(user);
    }
}
