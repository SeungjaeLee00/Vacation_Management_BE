package com.example.vacation_reservation.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.vacation_reservation.entity.User;
import com.example.vacation_reservation.exception.CustomException;
import com.example.vacation_reservation.repository.UserRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 amazonS3;

    private final String bucketName = "2025ojt.leave-application";

    /**
     * S3에 이미지 파일 업로드
     *
     * @param file 클라이언트로부터 전달받은 이미지 파일 (MultipartFile)
     * @return 업로드된 이미지의 URL (https 프로토콜)
     * @throws IOException 이미지 처리 실패 시 예외 발생
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 파일에서 BufferedImage 객체 생성 (이미지 유효성 검사)
        BufferedImage image = ImageIO.read(file.getInputStream());

        // 이미지가 아닐 경우 커스텀 예외 던짐
        if (image == null) {
            throw new CustomException("유효한 이미지 파일이 아닙니다.");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        // 최소 크기 조건 (200x200)
        if (width < 200 || height < 200) {
            throw new CustomException("이미지 크기가 너무 작습니다. 최소 200x200 픽셀 이상이어야 합니다.");
        }
        // 최대 크기 조건 (3000x3000)
        if (width > 3000 || height > 3000) {
            throw new CustomException("이미지 크기가 너무 큽니다. 최대 3000x3000 픽셀 이하여야 합니다.");
        }

        // 원본 파일 확장자 추출 (png 또는 jpg만 허용)
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new CustomException("파일 이름이 없습니다.");
        }

        String extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();
        if (!extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("png")) {
            throw new CustomException("JPG 또는 PNG 형식만 업로드 가능합니다.");
        }

        // 저장할 포맷 확정 (jpeg 또는 png)
        String scaledImg = extension.equals("png") ? "png" : "jpg";

        // S3 내 저장할 파일명 생성
        // 폴더명 + UUID + 원본 파일명 조합 (중복 방지 목적!)
        String fileName = "profile-images/" + UUID.randomUUID().toString() + "-" + scaledImg;

        // 썸네일 처리
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Thumbnails.of(image)
                .size(800, 800)                 // 최대 800x800 크기로 리사이즈 (비율 유지)
                .outputFormat(scaledImg)
                .outputQuality(0.8)
                .toOutputStream(baos);

        // 리사이즈된 이미지 데이터를 바이트 배열로 받기
        byte[] resizedImageBytes = baos.toByteArray();
        InputStream is = new ByteArrayInputStream(resizedImageBytes);

        // S3에 전송할 메타데이터 설정 (길이, 콘텐츠 타입)
        String contentType = scaledImg.equals("png") ? MediaType.IMAGE_PNG_VALUE : MediaType.IMAGE_JPEG_VALUE;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(resizedImageBytes.length);
        metadata.setContentType(contentType);

        // S3에 파일 업로드 수행
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, is, metadata));

        // 업로드한 파일의 HTTPS URL 리턴
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    /**
     * S3에 저장된 파일 삭제
     *
     * @param fileUrl 삭제할 파일의 URL (HTTP 또는 HTTPS)
     */
    public void deleteFile(String fileUrl) {
        // URL에서 S3 객체 key 추출
        String fileKey = extractKeyFromUrl(fileUrl);

        // 기본 프로필은 삭제 안함
        if ("profile-images/ta9_logo.png".equals(fileKey)) {
            System.out.println("삭제 금지 파일입니다: " + fileKey);
            return;  // 삭제하지 않고 종료
        }

        try {
            // System.out.println("객체 key: " + fileKey);
            amazonS3.deleteObject(bucketName, fileKey);
        } catch (Exception e) {
            throw new CustomException("S3 파일 삭제 실패: " + e.getMessage());
        }
    }

    /**
     * URL에서 S3 객체 키만 추출 (버킷명 제외한 경로 부분)
     * URL 인코딩된 경우도 디코딩 처리
     *
     * @param fileUrl S3 객체의 전체 URL
     * @return S3 버킷 내 객체 키 (예: profile-images/UUID-파일명.jpg)
     */
    private String extractKeyFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath();

            // URL 경로의 앞에 '/'가 있으면 제거
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            // URL 디코딩 수행 (한글 등 인코딩 문자 변환)
            String decodedKey = URLDecoder.decode(path, StandardCharsets.UTF_8.toString());
            return decodedKey;
        } catch (Exception e) {
            throw new CustomException("잘못된 이미지 URL입니다: " + e.getMessage());
        }
    }

    /**
     * 프로필 이미지 업데이트 처리
     * 1) 기존 이미지가 있으면 삭제
     * 2) 새 이미지 업로드
     * 3) 업로드된 이미지 URL을 HTTP URL로 변환하여 사용자 정보 업데이트 후 저장
     *
     * @param user 기존 User 엔티티 객체 (프로필 이미지 URL 포함)
     * @param newImage 새로 업로드할 MultipartFile 이미지
     * @param userRepository User 엔티티를 저장할 리포지토리
     */
    @Transactional
    public void updateProfileImage(User user, MultipartFile newImage, UserRepository userRepository) {
        try {
            // 기존 이미지 URL 조회
            String oldImageUrl = user.getProfileImageUrl();

            // 기존 이미지가 존재하면 삭제 시도
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                try {
                    deleteFile(oldImageUrl);
                } catch (Exception e) {
                    System.err.println("기존 이미지 삭제 실패: " + e.getMessage());
                }
            }

            // 새 이미지 업로드 후 HTTPS URL 받기
            String httpsUrl = uploadFile(newImage);

            // HTTPS URL을 HTTP 웹사이트 엔드포인트 URL로 변환
            String httpUrl = convertToHttpUrl(httpsUrl);

            // User 객체에 새 프로필 이미지 URL 저장 및 DB 반영
            user.setProfileImageUrl(httpUrl);
            userRepository.save(user);

        } catch (IOException e) {
            throw new CustomException("이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * HTTPS URL을 HTTP S3 웹사이트 엔드포인트 URL로 변환
     * AWS S3 버킷 호스트명 차이로 인해 변환이 필요함
     *
     * @param httpsUrl HTTPS 프로토콜 URL
     * @return HTTP 프로토콜로 변경된 S3 웹사이트 엔드포인트 URL
     */
    private String convertToHttpUrl(String httpsUrl) {
        return httpsUrl.replace(
                "https://2025ojt.leave-application.s3.ap-northeast-2.amazonaws.com",
                "http://2025ojt.leave-application.s3-website.ap-northeast-2.amazonaws.com"
        );
    }
}
