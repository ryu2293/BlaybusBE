package com.blaybus.blaybusbe.domain.studyContent.service;

import com.blaybus.blaybusbe.domain.studyContent.dto.response.ResponseContentDto;
import com.blaybus.blaybusbe.domain.studyContent.entitiy.StudyContents;
import com.blaybus.blaybusbe.domain.studyContent.enums.Subject;
import com.blaybus.blaybusbe.domain.studyContent.repository.StudyContentRepository;
import com.blaybus.blaybusbe.domain.user.entity.User;
import com.blaybus.blaybusbe.domain.user.repository.UserRepository;
import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import com.blaybus.blaybusbe.global.s3.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyContentService {

    private final StudyContentRepository studyContentRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    /**
     * 멘토가 학습자료(PDF)를 업로드하여 저장한다.
     *
     * @param mentorId 멘토 id
     * @param title    제목
     * @param subject  과목
     * @param file     파일
     */
    public Long createStudyContent(Long mentorId, String title, Subject subject, MultipartFile file) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // pdf 파일 S3에 업로드
        String contentUrl = null;
        if (file != null && !file.isEmpty()) {
            contentUrl = s3Service.uploadStudyPdf(file);
        }

        StudyContents studyContents = StudyContents.builder()
                .title(title)
                .subject(subject)
                .contentUrl(contentUrl)
                .mentor(mentor)
                .build();

        return studyContentRepository.save(studyContents).getId();
    }

    /**
     * 멘토가 등록한 학습자료 목록 조회(mentor_id가 null이면 설스터디측에서 제공한 공식 자료임)
     *
     * @param mentorId 멘토 id
     * @param pageable 페이지네이션
     */
    public Page<ResponseContentDto> getStudyContents(Long mentorId, Pageable pageable) {
        Page<StudyContents> contentsPage = studyContentRepository.findAllByMentorIdOrAdmin(mentorId, pageable);
        return contentsPage.map(content -> ResponseContentDto.from(content));
    }

    /**
     * 등록한 학습 자료를 삭제합니다.
     *
     * @param mentorId  멘토 id
     * @param contentId 삭제할 학습자료 id
     */
    public void deleteStudyContent(Long mentorId, Long contentId) {
        StudyContents content = studyContentRepository.findById(contentId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));

        // 공식 자료(mentor가 null)인 경우 삭제 불가
        if (content.getMentor() == null) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 본인이 등록한 자료가 아닌 경우 삭제 불가
        if (!content.getMentor().getId().equals(mentorId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        studyContentRepository.delete(content);
    }
}
