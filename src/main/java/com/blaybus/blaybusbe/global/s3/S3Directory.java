package com.blaybus.blaybusbe.global.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum S3Directory {
    PROFILE("profile/"),
    ASSIGNMENT("assignment/"),
    STUDY_PDF("study-pdf/"),
    FEEDBACK("feedback/");

    private final String path;
}