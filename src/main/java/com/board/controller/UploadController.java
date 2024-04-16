package com.board.controller;

import com.board.dto.UploadResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class UploadController {

    @Value("${com.example.upload.path}") // application.properties의 변수
    private String uploadPath;

    @PostMapping("/api/upload")
    public ResponseEntity<List<UploadResultDTO>> uploadFile(MultipartFile[] uploadFiles){
        // uploadFiles 배열이 null인지 확인
        if (uploadFiles == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 클라이언트 오류 반환
        }

        List<UploadResultDTO> resultDTOList = new ArrayList<>();
        for (MultipartFile uploadFile : uploadFiles) {
            // 이미지 파일만 업로드 가능
            if (!uploadFile.getContentType().startsWith("image")) {
                // 이미지가 아닌 경우
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            // 실제 파일 이름 IE나 Edge는 전체 경로가 들어오므로
            String originalName = uploadFile.getOriginalFilename();

            // 파일 이름 추출
            String fileName = Paths.get(originalName != null ? originalName : "").getFileName().toString();

            // 날짜 폴더 생성
            String folderPath = makeFolder();

            // UUID 생성
            String uuid = UUID.randomUUID().toString();

            // 저장할 파일 경로 생성
            String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + fileName;

            Path savePath = Paths.get(saveName);

            try {
                // 파일 저장
                uploadFile.transferTo(savePath);
                // 결과 DTO 추가
                resultDTOList.add(new UploadResultDTO(fileName, uuid, folderPath));
            } catch (IOException e){
                e.printStackTrace();
                // 파일 저장 실패 시 예외 처리
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // 모든 파일이 이미지인 경우에만 정상적으로 처리됨
        return new ResponseEntity<>(resultDTOList, HttpStatus.OK);
    }


    private String makeFolder() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("/", File.separator);

        // 폴더가 이미 존재하는지 확인
        File uploadPathFolder = new File(uploadPath, folderPath);
        if (!uploadPathFolder.exists()) { // 폴더가 존재하지 않는 경우에만 생성
            if (!uploadPathFolder.mkdirs()) { // 폴더 생성 실패 시 오류 처리
                throw new RuntimeException("Failed to create upload folder: " + uploadPathFolder.getAbsolutePath());
            }
        }

        return folderPath;
    }
}