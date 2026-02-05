package com.blaybus.blaybusbe.domain.mentoring.service;

import com.blaybus.blaybusbe.domain.mentoring.dto.response.ResponseMenteeInfoDto;
import com.blaybus.blaybusbe.domain.mentoring.dto.response.ResponseMyMentorDto;
import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.domain.user.repository.UserRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MenteeInfoService {

    private final MenteeInfoRepository menteeInfoRepository;
    private final UserRepository userRepository;

    /**
     * 멘토와 맵핑된 멘티 목록 조회
     *
     * @param mentorId 멘토 id
     * @param pageable 페이지네이션
     */
    @Transactional(readOnly = true)
    public Page<ResponseMenteeInfoDto> findMyMentees(Long mentorId, Pageable pageable) {

        Page<MenteeInfo> mentees = menteeInfoRepository.searchMyMentees(mentorId, pageable);

        return mentees.map(menteeInfo -> ResponseMenteeInfoDto.from(menteeInfo));
    }

    /**
     * 멘티와 매핑된 멘토 조회
     *
     * @param menteeId 멘티 id
     * @return 멘토 정보 리턴
     */
    @Transactional(readOnly = true)
    public ResponseMyMentorDto findMyMentor(Long menteeId) {
        MenteeInfo menteeInfo = menteeInfoRepository.findByMenteeIdWithMentor(menteeId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENTEE_INFO_NOT_FOUND));

        return ResponseMyMentorDto.from(menteeInfo.getMentor());
    }
}