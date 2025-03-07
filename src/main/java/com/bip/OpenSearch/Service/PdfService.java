package com.bip.OpenSearch.Service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PdfService {
    private final OpenSearchClient openSearchClient;

    public PdfService(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    public void processPdf(MultipartFile file) throws IOException {
        // Extract text from PDF
        String extractedText = extractTextFromPdf(file);

        // Store in OpenSearch
        Map<String, Object> document = new HashMap<>();
        document.put("content", extractedText);

        IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                .index("documents") // Store PDFs in a separate index
                .document(document)
        );

        openSearchClient.index(request);
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            return pdfTextStripper.getText(document);
        }
    }
}
