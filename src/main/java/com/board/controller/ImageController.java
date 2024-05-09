package com.board.controller;

import com.board.common.ApiResponseDto;
import com.board.entity.Image;
import com.board.repository.ImageRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
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
        public ResponseEntity<String> predictBreed(@PathVariable Long id) throws Exception {
            Image image = imageRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid image ID: " + id));

            // 이미지 파일 경로
            Path path = Paths.get("C:/Users/skm99/OneDrive/Desktop/Img/" + image.getFileName());
            String imagePath = path.toString();

            // 파이썬 스크립트 경로
            String pythonScriptPath = "C:/Users/skm99/OneDrive/Desktop/pet.py";

            String result = "";
            // 파이썬 실행 명령
            String[] cmd = {"python", pythonScriptPath, imagePath};

            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process process = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            StringBuilder output = new StringBuilder();
            StringBuilder errorMessage = new StringBuilder();

            String lastLine = null;
            while ((line = in.readLine()) != null) {
                output.append(line).append("\n");
                lastLine = line; // 각 줄을 읽을 때마다 마지막 줄을 갱신합니다.
            }

            result = lastLine; // 마지막으로 읽은 줄을 결과로 설정합니다.

            while ((line = err.readLine()) != null) {
                errorMessage.append(line).append("\n");
            }

            int exitCode = process.waitFor();

//        System.out.println("Exit code: " + exitCode);
//            result = output.toString();
//        System.out.println("Error: " + errorMessage.toString());

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(result);
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
