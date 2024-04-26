package com.board.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@RestController
public class AIController {

    @GetMapping("/predict")
    public String predictBreed(@RequestParam String imagePath) {
        String pythonScriptPath = "/path/to/your/python/script.py";
        String[] cmd = new String[3];
        cmd[0] = "python3";
        cmd[1] = pythonScriptPath;
        cmd[2] = imagePath;

        String result = "";
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process process = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            result = in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Predicted breed: " + result;
    }
}

