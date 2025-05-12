package com.example.vacation_reservation.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 amazonS3;

    private final String bucketName = "2025ojt.leave-application";  // 버킷 이름

    /**
     * S3에 파일을 업로드하는 메서드
     *
     * <p>이 메서드는 주어진 파일을 S3에 업로드하고, 업로드된 파일의 URL을 반환.
     * 파일 이름은 UUID를 기반으로 고유하게 생성되며, S3 버킷에 저장됨.</p>
     *
     * @param file 업로드할 파일. MultipartFile 객체로 전달.
     * @return 업로드된 파일의 URL을 반환.
     */
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = "profile-images/" + UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // 임시 파일로 저장
//        File tempFile = File.createTempFile("temp", file.getOriginalFilename());
//        file.transferTo(tempFile);

        // InputStream을 사용하여 파일을 직접 업로드
        InputStream inputStream = file.getInputStream();

        // S3에 파일 업로드
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, null));

        // 업로드된 파일의 URL 반환
        return amazonS3.getUrl(bucketName, fileName).toString();
    }
}


// 나중에 디비에 저장할 때는 http로 저장해야함
// http://2025ojt.leave-application.s3-website.ap-northeast-2.amazonaws.com/profile-images/48b401fe-a84f-400a-9900-2dd7c5cc84a9-ta9_logo.png