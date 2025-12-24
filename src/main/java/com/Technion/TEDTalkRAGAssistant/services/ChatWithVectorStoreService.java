package com.Technion.TEDTalkRAGAssistant.services;
import com.Technion.TEDTalkRAGAssistant.dtos.*;

import com.Technion.TEDTalkRAGAssistant.dtos.TEDTalk;
import com.Technion.TEDTalkRAGAssistant.dtos.TEDTalkChunk;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.ai.chat.prompt.PromptTemplate;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatWithVectorStoreService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("${file.tedtalk.csv}")
    private String csvFilePath;

    @Value("${tedtalk.batch.size}")
    private int batchSize;

    @Value("${tedtalk.chunk.size}")
    private int chunkSize;

    @Value("${tedtalk.chunk.overlap}")
    private int chunkOverlap;

    @Value("${tedtalk.use.ai.chunking:false}")
    private boolean useAiChunking;

    @Value("${tedtalk.top.k:5}")
    private int topK;

    public ChatWithVectorStoreService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        List.of(
                                QuestionAnswerAdvisor.builder(this.vectorStore).build()
                        ))
                .build();
    }

//    @PostConstruct
//    public void initData() {
//        System.out.println("========================================");
//        System.out.println("Starting TED Talks loading process...");
//        System.out.println("CSV File: " + csvFilePath);
//        System.out.println("Batch Size: " + batchSize);
//        System.out.println("AI Chunking: " + (useAiChunking ? "Enabled" : "Disabled (Simple chunking)"));
//        if (!useAiChunking) {
//            System.out.println("Chunk Size: " + chunkSize + " characters");
//            System.out.println("Chunk Overlap: " + chunkOverlap + " characters");
//        }
//        System.out.println("========================================");
//
//        long startTime = System.currentTimeMillis();
//
//        List<Document> batch = new ArrayList<>();
//        int totalProcessed = 0;
//        int totalChunks = 0;
//        int errorCount = 0;
//
//        try (Reader reader = new FileReader(csvFilePath);
//             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
//
//            for (CSVRecord record : csvParser) {
//                try {
//                    String tedTalkContent = buildTedTalkContent(record);
//
//                    if (tedTalkContent != null && !tedTalkContent.isEmpty()) {
//                        TEDTalk tedTalk = optimizeTedTalk(tedTalkContent, record);
//
//                        for (TEDTalkChunk chunk : tedTalk.getChunks()) {
//                            Document doc = new Document(
//                                    chunk.getChunkContent(),
//                                    chunk.getKeywords()
//                            );
//                            batch.add(doc);
//                            totalChunks++;
//                        }
//                    }
//
//                    totalProcessed++;
//
//                    // Upload batch when it reaches batchSize
//                    if (batch.size() >= batchSize) {
//                        vectorStore.add(batch);
//                        System.out.println("✓ Uploaded batch: " + batch.size() + " chunks | Processed: " + totalProcessed + " talks | Total chunks: " + totalChunks);
//                        batch.clear();
//                    }
//
//                    // Progress update every 500 talks
//                    if (totalProcessed % 500 == 0) {
//                        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
//                        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
//                        System.out.println("Progress: " + totalProcessed + " talks processed");
//                        System.out.println("Time elapsed: " + elapsed + " seconds");
//                        System.out.println("Average: " + (totalProcessed > 0 ? elapsed / totalProcessed : 0) + " seconds per talk");
//                        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
//                    }
//
//                } catch (Exception e) {
//                    errorCount++;
//                    System.err.println("✗ Error processing talk #" + totalProcessed + " (ID: " + getFieldSafe(record, "talk_id") + "): " + e.getMessage());
//                }
//            }
//
//            // Upload remaining documents
//            if (!batch.isEmpty()) {
//                vectorStore.add(batch);
//                System.out.println("✓ Uploaded final batch: " + batch.size() + " chunks");
//            }
//
//            long totalTime = (System.currentTimeMillis() - startTime) / 1000;
//
//            System.out.println("========================================");
//            System.out.println("✓ LOADING COMPLETED SUCCESSFULLY");
//            System.out.println("========================================");
//            System.out.println("Total TED Talks processed: " + totalProcessed);
//            System.out.println("Total chunks created: " + totalChunks);
//            System.out.println("Average chunks per talk: " + (totalProcessed > 0 ? totalChunks / totalProcessed : 0));
//            System.out.println("Errors encountered: " + errorCount);
//            System.out.println("Total time: " + totalTime + " seconds (" + (totalTime / 60) + " minutes)");
//            System.out.println("========================================");
//
//        } catch (IOException e) {
//            System.err.println("✗ FATAL ERROR reading CSV file: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private String buildTedTalkContent(CSVRecord record) {
//        try {
//            return String.format(
//                    "Title: %s\n" +
//                            "Speaker: %s\n" +
//                            "Occupation: %s\n" +
//                            "About Speaker: %s\n" +
//                            "Event: %s\n" +
//                            "Topics: %s\n" +
//                            "url: %s\n\n" +
//                            "Description: %s\n\n" +
//                            "Transcript:\n%s",
//                    getFieldSafe(record, "title"),
//                    getFieldSafe(record, "all_speakers"),
//                    getFieldSafe(record, "occupations"),
//                    getFieldSafe(record, "about_speakers"),
//                    getFieldSafe(record, "event"),
//                    getFieldSafe(record, "topics"),
//                    getFieldSafe(record, "url"),
//                    getFieldSafe(record, "description"),
//                    getFieldSafe(record, "transcript")
//            );
//        } catch (Exception e) {
//            System.err.println("Error building content for record: " + e.getMessage());
//            return "";
//        }
//    }
//
//    private String getFieldSafe(CSVRecord record, String fieldName) {
//        try {
//            String value = record.get(fieldName);
//            return value != null ? value.trim() : "";
//        } catch (Exception e) {
//            return "";
//        }
//    }
//
//    private TEDTalk optimizeTedTalk(String content, CSVRecord record) {
//        if (useAiChunking) {
//            return optimizeTedTalkWithAI(content, record);
//        } else {
//            return optimizeTedTalkSimple(content, record);
//        }
//    }
//
//    /**
//     * AI-based chunking (slower, more intelligent)
//     * Use this for smaller datasets or when you need semantic chunking
//     */
//    private TEDTalk optimizeTedTalkWithAI(String content, CSVRecord record) {
//        try {
//            // Add small delay to avoid rate limits
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//
//        Prompt prompt = Prompt.builder()
//                .messages(
//                        new SystemMessage("You are a TED Talk parser that can parse TED Talk data and extract relevant information from it. You will receive a TED Talk with title, speaker info, description, and full transcript."),
//                        new SystemMessage("The result should be wrapped in a TEDTalk Object where the chunkContent will contain meaningful segments of the talk and keywords will contain the main topics, themes, and key concepts from that segment. This will be used for embedding the information for optimized RAG retrieval."),
//                        new UserMessage("Parse the following TED Talk and split the content into relevant chunks. Create chunks based on topic shifts, key arguments, stories, or natural breaks in the talk. Each chunk should be semantically coherent. Include relevant metadata like title, speaker, event, and topics. \n\nHere is the TED Talk:\n\n" + content)
//                )
//                .build();
//
//        TEDTalk tedTalk = this.chatClient.prompt(prompt).call().entity(TEDTalk.class);
//
//        // Add consistent metadata to all chunks
//        for (TEDTalkChunk chunk : tedTalk.getChunks()) {
//            Map<String, Object> keywords = chunk.getKeywords();
//            if (keywords == null) {
//                keywords = new HashMap<>();
//            }
//
//            // Add TED-specific metadata
//            keywords.put("talk_id", getFieldSafe(record, "talk_id"));
//            keywords.put("title", getFieldSafe(record, "title"));
//            keywords.put("speaker", getFieldSafe(record, "all_speakers"));
//            keywords.put("event", getFieldSafe(record, "event"));
//            keywords.put("topics", getFieldSafe(record, "topics"));
//            keywords.put("recorded_date", getFieldSafe(record, "recorded_date"));
//            keywords.put("views", getFieldSafe(record, "views"));
//
//            chunk.setKeywords(keywords);
//        }
//
//        return tedTalk;
//    }
//
//    /**
//     * Simple text-based chunking (fast, reliable)
//     * Recommended for large datasets (5000+ rows)
//     */
//    private TEDTalk optimizeTedTalkSimple(String content, CSVRecord record) {
//        TEDTalk tedTalk = new TEDTalk();
//        List<TEDTalkChunk> chunks = new ArrayList<>();
//
//        // Simple text-based chunking
//        for (int i = 0; i < content.length(); i += (chunkSize - chunkOverlap)) {
//            int end = Math.min(i + chunkSize, content.length());
//            String chunkText = content.substring(i, end);
//
//            // Try to end at a sentence boundary for better chunks
//            if (end < content.length() && !chunkText.endsWith(".") && !chunkText.endsWith("!") && !chunkText.endsWith("?")) {
//                int lastPeriod = chunkText.lastIndexOf('.');
//                int lastQuestion = chunkText.lastIndexOf('?');
//                int lastExclamation = chunkText.lastIndexOf('!');
//                int lastSentenceEnd = Math.max(lastPeriod, Math.max(lastQuestion, lastExclamation));
//
//                if (lastSentenceEnd > chunkSize / 2) {
//                    end = i + lastSentenceEnd + 1;
//                    chunkText = content.substring(i, end);
//                }
//            }
//
//            TEDTalkChunk chunk = new TEDTalkChunk();
//            chunk.setChunkContent(chunkText.trim());
//
//            // Create rich metadata for each chunk
//            Map<String, Object> metadata = new HashMap<>();
//            metadata.put("talk_id", getFieldSafe(record, "talk_id"));
//            metadata.put("title", getFieldSafe(record, "title"));
//            metadata.put("speaker", getFieldSafe(record, "all_speakers"));
//            metadata.put("occupations", getFieldSafe(record, "occupations"));
//            metadata.put("event", getFieldSafe(record, "event"));
//            metadata.put("topics", getFieldSafe(record, "topics"));
//            metadata.put("recorded_date", getFieldSafe(record, "recorded_date"));
//            metadata.put("published_date", getFieldSafe(record, "published_date"));
//            metadata.put("views", getFieldSafe(record, "views"));
//            metadata.put("duration", getFieldSafe(record, "duration"));
//            metadata.put("url", getFieldSafe(record, "url"));
//            metadata.put("chunk_index", chunks.size());
//            metadata.put("total_chunks", (content.length() / (chunkSize - chunkOverlap)) + 1);
//
//            chunk.setKeywords(metadata);
//            chunks.add(chunk);
//        }
//
//        tedTalk.setChunks(chunks);
//        return tedTalk;
//    }

    public List<Document> search(String query) {
        return this.vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(5)
                        .build()
        );
    }

    public String sendMessage(String prompt) {
        System.out.println("Sending prompt: " + prompt);
        String response = this.chatClient.prompt(prompt).call().content();
        System.out.println("Response: " + response);
        return response;
    }

    public PromptResponse processRAGQuery(String question) {
        try {
            // 1. Search for relevant chunks
            List<Document> relevantDocs = this.vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(question)
                            .topK(topK)
                            .build()
            );

            // 2. Build context from retrieved documents
            StringBuilder contextBuilder = new StringBuilder();
            List<ContextChunk> contextChunks = new ArrayList<>();

            for (int i = 0; i < relevantDocs.size(); i++) {
                Document doc = relevantDocs.get(i);
                Map<String, Object> metadata = doc.getMetadata();

                String talkId = metadata.getOrDefault("talk_id", "unknown").toString();
                String title = metadata.getOrDefault("title", "Unknown Title").toString();

                // Get document content
                String chunkContent;
                try {
                    chunkContent = doc.getText();
                } catch (Exception e) {
                    try {
                        chunkContent = doc.getFormattedContent();
                    } catch (Exception e2) {
                        chunkContent = doc.toString();
                    }
                }

                // Calculate similarity score - FIX: Handle both Float and Double
                double score = 1.0 - (i * 0.1); // Default: Decreasing score based on ranking
                if (metadata.containsKey("distance")) {
                    Object distanceObj = metadata.get("distance");
                    if (distanceObj instanceof Float) {
                        score = 1.0 - ((Float) distanceObj).doubleValue();
                    } else if (distanceObj instanceof Double) {
                        score = 1.0 - ((Double) distanceObj);
                    } else if (distanceObj instanceof Number) {
                        score = 1.0 - ((Number) distanceObj).doubleValue();
                    }
                }

                // Ensure score is between 0 and 1
                score = Math.max(0.0, Math.min(1.0, score));

                // Add to context list
                contextChunks.add(new ContextChunk(talkId, title, chunkContent, score));

                // Build context string for prompt
                contextBuilder.append(String.format("[TED Talk: %s]\n%s\n\n", title, chunkContent));
            }

            String contextString = contextBuilder.toString();

            // 3. Build the augmented prompts
            String systemPrompt =
                    "You are a TED Talk question-answering assistant. " +
                            "You must answer using ONLY the provided TED dataset context (metadata and transcript passages). " +
                            "Do NOT use external knowledge. " +
                            "If the answer cannot be determined from the context, respond exactly with: " +
                            "\"I don’t know based on the provided TED data.\" " +
                            "If the user asks for specific metadata fields (such as title, speaker, occupation, topics, or URL), " +
                            "extract and return those fields directly if present in the context. " +
                            "Do NOT say you don’t know if the field exists.";

            String userPrompt = String.format(
                    "TED DATA CONTEXT:\n%s\n\n" +
                            "QUESTION:\n%s\n\n" +
                            "INSTRUCTIONS:\n" +
                            "- Answer strictly using the TED data above.\n" +
                            "- If a requested field (e.g., URL) exists in the metadata, return it directly.\n" +
                            "- Do NOT use the fallback response if the information is present.\n",
                    contextString,
                    question
            );



            // 4. Query the chat model
            Prompt prompt = Prompt.builder()
                    .messages(
                            new SystemMessage(systemPrompt),
                            new UserMessage(userPrompt)
                    )
                    .build();

            String response = this.chatClient.prompt(prompt).call().content();

            // 5. Build the response object
            AugmentedPrompt augmentedPrompt = new AugmentedPrompt(systemPrompt, userPrompt);

            return new PromptResponse(response, contextChunks, augmentedPrompt);

        } catch (Exception e) {
            System.err.println("Error in processRAGQuery: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to process RAG query: " + e.getMessage(), e);
        }
    }

    public StatsResponse getStats() {
        // Calculate overlap ratio
        double overlapRatio = (double) chunkOverlap / chunkSize;

        return new StatsResponse(chunkSize, overlapRatio, topK);
    }
}