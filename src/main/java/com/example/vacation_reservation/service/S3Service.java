package com.example.vacation_reservation.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 amazonS3;

    private final String bucketName = "2025ojt.leave-application";  // 버킷 이름

    // 파일 업로드 메서드
    public String uploadFile(MultipartFile file) throws IOException {
        // 임시 파일로 저장
        File tempFile = File.createTempFile("temp", file.getOriginalFilename());
        file.transferTo(tempFile);

        // S3에 파일 업로드
        amazonS3.putObject(new PutObjectRequest(bucketName, file.getOriginalFilename(), tempFile));

        // 업로드된 파일의 URL 반환
        return amazonS3.getUrl(bucketName, file.getOriginalFilename()).toString();
    }

    // 파일 다운로드 메서드 (예시)
    public S3Object downloadFile(String fileName) {
        return amazonS3.getObject(bucketName, fileName);
    }
}
