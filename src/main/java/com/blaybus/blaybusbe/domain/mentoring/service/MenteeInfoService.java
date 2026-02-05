package com.blaybus.blaybusbe.domain.mentoring.service;

import com.blaybus.blaybusbe.domain.mentoring.dto.response.ResponseMenteeInfoDto;
import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
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
}