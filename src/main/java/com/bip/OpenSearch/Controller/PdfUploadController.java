package com.bip.OpenSearch.Controller;

import com.bip.OpenSearch.Service.PdfService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
public class PdfUploadController {
    private final PdfService pdfService;

    public PdfUploadController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/upload")
    public String uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            pdfService.processPdf(file);
            return "PDF uploaded and indexed successfully!";
        } catch (IOException e) {
            return "Failed to process PDF: " + e.getMessage();
        }
    }
}
