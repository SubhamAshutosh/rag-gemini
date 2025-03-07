package com.bip.OpenSearch.Service;

import com.bip.OpenSearch.Config.GeminiClient;
import com.bip.OpenSearch.Entity.Document;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RagService {
    private final OpenSearchClient openSearchClient;
    private final ChatClient chatClient;
    private final GeminiClient geminiClient;

    public RagService(OpenSearchClient openSearchClient, ChatClient.Builder chatClientBuilder, GeminiClient geminiClient) {
        this.openSearchClient = openSearchClient;
        this.chatClient = chatClientBuilder.build();
        this.geminiClient = geminiClient;
    }

    public String askQuestion(String query) {
        try {
            // Construct the Search Request
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("documents") // Index Name
                    .query(q -> q.match(m -> m.field("content").query(FieldValue.of(query))))
            );

            // Execute the search query in OpenSearch
            SearchResponse<Document> searchResponse = openSearchClient.search(searchRequest, Document.class);

            // Extract document contents and join them into a single string
            String retrievedDocs = searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .map(Document::getContent)
                    .collect(Collectors.joining("\n"));

            // Define system prompt template
            String systemPrompt = """
                You are an AI assistant who provides responses strictly based on the retrieved documents.
                If the answer is not found, respond with: "The requested information is not found in the documents."
                
                Document(s):
                {documents}
                
                User Query: {query}
                """;

            // Replace placeholders using Map.of()
            PromptTemplate promptTemplate = new PromptTemplate(systemPrompt, Map.of(
                    "documents", retrievedDocs,
                    "query", query
            ));

            // Generate final prompt string
            String finalPrompt = promptTemplate.createMessage().toString();

            // Call Gemini AI with the constructed prompt
            return geminiClient.generateResponse(finalPrompt);
        } catch (IOException e) {
            throw new RuntimeException("Error querying OpenSearch", e);
        }
    }
}
