package com.blaybus.blaybusbe.global.s3;

import com.blaybus.blaybusbe.global.exception.CustomException;
import com.blaybus.blaybusbe.global.exception.error.ErrorCode;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 유저 프로필 업로드 전용 메서드
     * @param file
     */
    public String uploadProfileImage(MultipartFile file) {
        return upload(file, S3Directory.PROFILE.getPath());
    }

    /**
     * 공통 업로드 메서드
     * @param file 업로드할 파일
     * @param directory 파일 구조(프로필, 학습자료 pdf ...)
     * @return
     */
    private String upload(MultipartFile file, String directory) {
        try {
            // 파일명 생성: 폴더명 + UUID + 원본파일명
            String fileName = directory + UUID.randomUUID() + "_" + file.getOriginalFilename();

            var resource = s3Template.upload(bucket, fileName, file.getInputStream(),
                    ObjectMetadata.builder().contentType(file.getContentType()).build());

            return resource.getURL().toString();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.S3_UPLOAD_ERROR);
        }
    }
}
