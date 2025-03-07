package com.bip.OpenSearch.Controller;

import com.bip.OpenSearch.Service.DocumentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/add")
    public String addDocument(@RequestBody String content) {
        documentService.addDocument(content);
        return "Document added successfully!";
    }
}
