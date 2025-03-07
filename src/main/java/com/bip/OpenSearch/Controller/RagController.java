package com.bip.OpenSearch.Controller;

import com.bip.OpenSearch.Service.RagService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
public class RagController {
    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/ask")
    public String askQuestion(@RequestParam String question) {
        return ragService.askQuestion(question);
    }
}
