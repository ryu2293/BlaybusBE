package com.blaybus.blaybusbe.global.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3Uploader {
    String uploadProfileImage(MultipartFile file);
    String uploadStudyPdf(MultipartFile file);
    String uploadAssignmentImage(MultipartFile file);
    String uploadFeedbackImage(MultipartFile file);
}
