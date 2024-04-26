package com.board;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.*;

public class Test {
    public static void main(String[] args) throws Exception {
    String pythonScriptPath = "C:/Users/skm99/OneDrive/Desktop/수업자료/spring boot/testSpring/src/main/java/com/board/pet.py";

    // 이미지 파일 경로
    String imagePath = "C:/Users/skm99/OneDrive/Desktop/img/KakaoTalk_20240507_175044614.jpg";

    // 파이썬 실행 명령
    String[] cmd = new String[3];
    cmd[0] = "python"; // 파이썬 버전에 따라 "python" 또는 "python3"를 사용
    cmd[1] = pythonScriptPath;
    cmd[2] = imagePath; // 이미지 파일 경로를 파이썬 스크립트에 인자로 전달

    // 프로세스 빌더 생성 및 실행
    ProcessBuilder pb = new ProcessBuilder(cmd);
    Process process = pb.start();

    // 스크립트의 출력 가져오기
    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
    BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));

    // 프로세스 종료 대기
    int exitCode = process.waitFor();

    // 출력 및 오류 메시지 읽기
    String ret = in.readLine();
    String errMessage = err.readLine();

    System.out.println("Exit code: " + exitCode);
    System.out.println("Output: " + ret);
    System.out.println("Error: " + errMessage);
}
}

//    public static void main(String[] args) throws Exception {
//        // 파이썬 스크립트 경로
//        String pythonScriptPath = "C:/Users/skm99/OneDrive/Desktop/수업자료/spring boot/testSpring/src/main/java/com/board/pet.py";
//
//        // 이미지 파일 경로
//        String imagePath = "C:/Users/skm99/OneDrive/Desktop/img/KakaoTalk_20240507_175044614.jpg";
//
//        // 파이썬 실행 명령
//        String[] cmd = new String[3];
//        cmd[0] = "python"; // 파이썬 버전에 따라 "python" 또는 "python3"를 사용
//        cmd[1] = pythonScriptPath;
//        cmd[2] = imagePath; // 이미지 파일 경로를 파이썬 스크립트에 인자로 전달
//
//        // 프로세스 빌더 생성 및 실행
//        ProcessBuilder pb = new ProcessBuilder(cmd);
//        Process process = pb.start();
//
//        // 스크립트의 출력 가져오기
//        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        String ret = in.readLine();
//        System.out.println("Predicted breed: " + ret);
//    }
//}
//        public static void main(String[] args) {
//            String pythonScriptPath = "C:/Users/skm99/OneDrive/Desktop/수업자료/spring boot/testSpring/src/main/java/com/board/pet.py";
//            String imagePath = "C:/Users/skm99/OneDrive/Desktop/img/KakaoTalk_20240507_175044614.jpg"; // 이미지 경로 설정
//            String outputFile = "output.txt";
//            String errorFile = "error.txt";
//
//            CommandLine cmdLine = new CommandLine("python");
//            cmdLine.addArgument(pythonScriptPath);
//            cmdLine.addArgument(imagePath); // 이미지 경로를 인자로 추가
//
//            try {
//                // 출력을 파일로 리디렉션
//                File out = new File(outputFile);
//                File err = new File(errorFile);
//                PumpStreamHandler streamHandler = new PumpStreamHandler(new FileOutputStream(out), new FileOutputStream(err));
//                DefaultExecutor executor = new DefaultExecutor();
//                executor.setStreamHandler(streamHandler);
//
//                // 타임아웃 설정
//                ExecuteWatchdog watchdog = new ExecuteWatchdog(600000);
//                executor.setWatchdog(watchdog);
//
//                // 스크립트 실행
//                int exitValue = executor.execute(cmdLine);
//                System.out.println("Exit value: " + exitValue);
//
//                // 결과 파일에서 필요한 정보 읽기
//                readOutputFromFile(outputFile);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        private static void readOutputFromFile(String fileName) throws IOException {
//            FileInputStream fis = new FileInputStream(fileName);
//            byte[] data = new byte[fis.available()];
//            fis.read(data);
//            fis.close();
//
//            String output = new String(data);
//            System.out.println("File Output: " + output);
//        }
//    }
