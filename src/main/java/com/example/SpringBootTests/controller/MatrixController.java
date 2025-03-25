package com.example.SpringBootTests.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Random;

@RestController
@RequestMapping("/api/matrix")
public class MatrixController {
    @Autowired
    private RestTemplate restTemplate;

    //URL of the second service
    @Value("${sort.service.url}")
    private String sortServiceUrl;

    private int[][] generateMatrix(int x, int y) {
        int[][] matrix = new int[x][y];
        Random r = new Random();
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                matrix[i][j] = r.nextInt(1000);
            }
        }
        return matrix;
    }

    @GetMapping("/distributed-sort")
    public ResponseEntity<String> distributedSort() {
        int rows = 200, cols = 200;
        int[][] matrix = generateMatrix(rows, cols);

        int mid = rows / 2;
        int[][] firstHalf = Arrays.copyOfRange(matrix, 0, mid);
        int[][] secondHalf = Arrays.copyOfRange(matrix, mid, rows);

        long startTime = System.currentTimeMillis();

        Arrays.sort(firstHalf, (a, b) -> Integer.compare(a[0], b[0]));

        long secondStartTime = System.currentTimeMillis();
        int[][] sortedSecond = restTemplate.postForObject(sortServiceUrl, secondHalf, int[][].class);
        long secondEndTime = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        sb.append("Initial Matrix:<br>\n").append(matrixToString(matrix));
        sb.append("<br><br>\n\nFirst Half Sorted Locally:<br>\n").append(matrixToString(firstHalf));



        if (sortedSecond != null) {
            sb.append("<br><br>\n\nSecond Half Sorted Remotely:<br>\n").append(matrixToString(sortedSecond));
            sb.append("<br><br>\n\nTime taken to sort second (ms): ").append(secondEndTime - secondStartTime);

            int[][] result = mergeMatrix(firstHalf, sortedSecond);

            long endTime = System.currentTimeMillis();

            sb.append("<br><br>\n\nMerged Matrix:<br>\n").append(matrixToString(result));
            sb.append("<br><br>\n\nTime taken to sort All (ms): ").append(endTime - startTime);

            return ResponseEntity.ok(sb.toString());
        } else {
            sb.append("<br><br>\n\nUnexpected error with POST query<br>\n");

            return ResponseEntity.ok(sb.toString());
        }

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

    private int[][] mergeMatrix(int[][] a, int[][] b) {
        int[][] result = new int[a.length + b.length][];
        int i = 0, j = 0, k = 0;
        while (i < a.length && j < b.length) {
            if (a[i][0] <= b[j][0]) {
                result[k++] = a[i++];
            } else {
                result[k++] = b[j++];
            }
        }
        while (i < a.length) {
            result[k++] = a[i++];
        }
        while (j < b.length) {
            result[k++] = b[j++];
        }
        return result;
    }

    private String matrixToString(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : matrix) {
            for (int val : row) {
                sb.append(val).append("\t");
            }
            sb.append("<br>\n");
        }
        return sb.toString();
    }
}
