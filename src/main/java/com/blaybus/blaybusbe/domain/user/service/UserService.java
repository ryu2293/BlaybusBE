package com.blaybus.blaybusbe.domain.user.service;

import com.blaybus.blaybusbe.domain.user.dto.response.ResponseUserDto;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.domain.user.repository.UserRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private static final String BASIC_PROFILE_IMAGE =
            "https://seolstudy-s3-bucket.s3.ap-northeast-2.amazonaws.com/profile/%E1%84%80%E1%85%B5%E1%84%87%E1%85%A9%E1%86%AB%E1%84%91%E1%85%B3%E1%84%85%E1%85%A9%E1%84%91%E1%85%B5%E1%86%AF.jpg";

    /**
     * 회원 정보 조회 로직
     *
     * @param userId: 유저아이디
     * @return
     */
    public ResponseUserDto findUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return ResponseUserDto.from(user);
    }

    /**
     * 프로필 이미지 업로드
     * @param userId   유저 id
     * @param imageUrl 프로필 이미지
     */
    public String updateProfileImage(Long userId, String imageUrl) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // imageUrl이면 자동으로 기본프로필로 설정됨.
        String targetUrl = (imageUrl == null || imageUrl.isBlank()) ? BASIC_PROFILE_IMAGE : imageUrl;

        user.setProfileImgUrl(targetUrl);
        return targetUrl;
    }

}
