package com.example.SpringBootTests.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api/matrix")
public class MatrixController {
    public int[][] generateMatrix(int x, int y) {
        int[][] matrix = new int[x][y];
        Random r = new Random();
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                matrix[i][j] = r.nextInt(1000);
            }
        }
        return matrix;
    }

    @GetMapping
    public String printMatrix(){
        int[][] matrix = generateMatrix(10, 10);
        String res = "";
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                res += matrix[i][j] + " ";
            }
            res += "<br>";
        }
        return res;
    }
}
