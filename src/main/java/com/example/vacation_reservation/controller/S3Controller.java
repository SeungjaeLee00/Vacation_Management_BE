//package com.example.vacation_reservation.controller;
//
//import com.example.vacation_reservation.service.S3Service;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequestMapping("/api/files")
//public class S3Controller {
//
//    @Autowired
//    private S3Service s3Service;
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
//        try {
//            // 파일 업로드
//            String fileUrl = s3Service.uploadFile(file);
//            return new ResponseEntity<>(fileUrl, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>("파일 업로드를 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//}
