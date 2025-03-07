package com.bip.OpenSearch.Service;

import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class DocumentService {
    private final OpenSearchClient openSearchClient;

    public DocumentService(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    public void addDocument(String content) {
        try {
            Map<String, Object> document = new HashMap<>();
            document.put("content", content);

            IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                    .index("documents")  // Index name in OpenSearch
                    .document(document)
            );

            openSearchClient.index(request);
        } catch (IOException e) {
            throw new RuntimeException("Error indexing document", e);
        }
    }
}
