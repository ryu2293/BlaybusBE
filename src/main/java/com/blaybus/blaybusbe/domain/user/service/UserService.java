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

    /**
     * 회원 정보 조회 로직
     * @param userId: 유저아이디
     * @return
     */
    public ResponseUserDto findUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow();

        return ResponseUserDto.from(user);
    }
}
