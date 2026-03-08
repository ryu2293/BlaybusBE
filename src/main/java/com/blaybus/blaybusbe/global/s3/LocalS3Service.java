package com.blaybus.blaybusbe.global.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * local 프로필에서 S3 업로드를 Mock 처리합니다.
 * 실제 AWS 연결 없이 더미 URL을 반환합니다.
 */
@Slf4j
@Service
@Profile("local")
public class LocalS3Service implements S3Uploader {

    private static final String DUMMY_BASE = "http://localhost/mock-s3/";

    @Override
    public String uploadProfileImage(MultipartFile file) {
        return mockUpload("profile", file);
    }

    @Override
    public String uploadStudyPdf(MultipartFile file) {
        return mockUpload("study-pdf", file);
    }

    @Override
    public String uploadAssignmentImage(MultipartFile file) {
        return mockUpload("assignment", file);
    }

    @Override
    public String uploadFeedbackImage(MultipartFile file) {
        return mockUpload("feedback", file);
    }

    private String mockUpload(String directory, MultipartFile file) {
        String dummyUrl = DUMMY_BASE + directory + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        log.info("[LocalS3Service] Mock 업로드: {}", dummyUrl);
        return dummyUrl;
    }
}
