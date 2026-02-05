package com.blaybus.blaybusbe.domain.studyContent.service;

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
     * @param title 제목
     * @param subject 과목
     * @param file 파일
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
}
