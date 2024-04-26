package com.board.controller;

import com.board.common.ApiResponseDto;
import com.board.entity.Image;
import com.board.repository.ImageRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    @PostMapping
    public ApiResponseDto<Long> uploadImage(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        Path path = Paths.get("C:/Users/skm99/OneDrive/Desktop/Img/" + fileName);
        Files.write(path, multipartFile.getBytes());

        Image image = new Image();
        image.setFileName(fileName);
        imageRepository.save(image);

        return ApiResponseDto.<Long>builder()
                .success(true)
                .response(image.getId())
                .build();
    }

        @GetMapping("/{id}")
        public ResponseEntity<String> predictBreed(@PathVariable Long id) throws IOException {
            Image image = imageRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid image ID: " + id));

            // 이미지 파일 경로
            Path path = Paths.get("C:/Users/skm99/OneDrive/Desktop/Img" + image.getFileName());
            String imagePath = path.toString();

            // 파이썬 스크립트 경로
            String pythonScriptPath = "C:Users/skm99/OneDrive/Desktop/품종구별2.ipynb";

            // 파이썬 실행 명령
            String[] cmd = new String[3];
            cmd[0] = "python"; // 파이썬 버전에 따라 "python" 또는 "python3"를 사용
            cmd[1] = pythonScriptPath;
            cmd[2] = imagePath; // 이미지 파일 경로를 파이썬 스크립트에 인자로 전달

            // 결과 초기화
            String result = "";

            try {
                // 프로세스 빌더 생성 및 실행
                ProcessBuilder pb = new ProcessBuilder(cmd);
                Process process = pb.start();

                // 스크립트의 출력 가져오기
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                result = in.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Predicted breed: " + result);
        }
//    @GetMapping("/{id}")
//    public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
//        Image image = imageRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid image ID: " + id));
//        //파일 경로
//        Path path = Paths.get("C:/Users/skm99/OneDrive/Desktop/Img" + image.getFileName());
//        byte[] imageBytes = Files.readAllBytes(path);
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG)
//                .body(imageBytes);
//    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        } else {
            return null;
        }
    }

    private MediaType getMediaTypeForImageExtension(String extension) {
        switch (extension) {
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            default:
                return MediaType.IMAGE_JPEG;
        }
    }
}
