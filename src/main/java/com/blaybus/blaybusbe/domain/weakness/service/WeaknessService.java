package com.blaybus.blaybusbe.domain.weakness.service;

import com.blaybus.blaybusbe.domain.mentoring.entity.MenteeInfo;
import com.blaybus.blaybusbe.domain.mentoring.repository.MenteeInfoRepository;
import com.blaybus.blaybusbe.domain.studyContent.entitiy.StudyContents;
import com.blaybus.blaybusbe.domain.studyContent.repository.StudyContentRepository;
import com.blaybus.blaybusbe.domain.weakness.dto.request.RequestWeaknessDto;
import com.blaybus.blaybusbe.domain.weakness.dto.response.ResponseWeaknessDto;
import com.blaybus.blaybusbe.domain.weakness.entitiy.Weakness;
import com.blaybus.blaybusbe.domain.weakness.repository.WeaknessRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class WeaknessService {

    private final WeaknessRepository weaknessRepository;
    private final MenteeInfoRepository menteeInfoRepository;
    private final StudyContentRepository studyContentRepository;

    /**
     * 멘토가 멘티의 보완점을 등록합니다.
     *
     * @param mentorId 멘토 id
     * @param request 제목, 멘티 id, 학습자료 id
     * @return
     */
    public Long createWeakness(Long mentorId, RequestWeaknessDto request) {
        MenteeInfo menteeInfo = menteeInfoRepository.findByMentorIdAndMenteeId(mentorId, request.menteeId())
                .orElseThrow(() -> new CustomException(ErrorCode.MENTEE_INFO_NOT_FOUND));

        StudyContents studyContent = null;
        if (request.contentId() != null) {
            studyContent = studyContentRepository.findById(request.contentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));
        }

        Weakness weakness = Weakness.builder()
                .title(request.title())
                .menteeInfo(menteeInfo)
                .studyContent(studyContent)
                .build();

        return weaknessRepository.save(weakness).getId();
    }

    /**
     * 멘토가 멘티의 보완점 목록 조회
     *
     * @param mentorId 멘토 id
     * @param menteeId 멘티 id
     * @param pageable 페이지네이션
     */
    public Page<ResponseWeaknessDto> getMenteeWeaknesses(Long mentorId, Long menteeId, Pageable pageable) {
        // 멘토, 멘티 관계 검증
        MenteeInfo menteeInfo = menteeInfoRepository.findByMentorIdAndMenteeId(mentorId, menteeId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENTEE_INFO_NOT_FOUND));

        return weaknessRepository.findAllByMenteeInfo(menteeInfo, pageable)
                .map(response -> ResponseWeaknessDto.from(response));
    }

    /**
     * 멘티가 본인의 보완점 목록 조회
     *
     * @param menteeId 멘티 id
     * @param pageable 페이지네이션
     */
    public Page<ResponseWeaknessDto> getMyWeaknesses(Long menteeId, Pageable pageable) {
        // 멘티 정보를 찾음
        MenteeInfo menteeInfo = menteeInfoRepository.findByMenteeId(menteeId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENTEE_INFO_NOT_FOUND));

        // 해당 멘티의 약점 페이징 조회
        return weaknessRepository.findAllByMenteeInfo(menteeInfo, pageable)
                .map(response -> ResponseWeaknessDto.from(response));
    }

    /**
     * 멘토가 멘티의 보완점을 삭제합니다.
     *
     * @param mentorId 멘토 id
     * @param weaknessId 보완점 id
     */
    public void deleteWeakness(Long mentorId, Long weaknessId) {
        Weakness weakness = weaknessRepository.findById(weaknessId)
                .orElseThrow(() -> new CustomException(ErrorCode.WEAKNESS_NOT_FOUND));

        // 약점이 속한 멘티정보의 담당 멘토가 요청자와 일치하는지 확인
        if (!weakness.getMenteeInfo().getMentor().getId().equals(mentorId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        weaknessRepository.delete(weakness);
    }
}
