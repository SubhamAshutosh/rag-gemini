package com.bip.OpenSearch.Controller;

import com.bip.OpenSearch.Service.ExcelService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/excel")
public class ExcelUploadController {
    private final ExcelService excelService;

    public ExcelUploadController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            excelService.processExcel(file);
            return "Excel file uploaded and indexed successfully!";
        } catch (IOException e) {
            return "Failed to process Excel file: " + e.getMessage();
        }
    }
}
