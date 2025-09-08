package me.springai.playground.qa.inference.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InferenceService {

    private final ChatClient chatClient;

    public InferenceService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Map<String, Object> answer(String question) {
        ChatClientResponse resp = chatClient
                .prompt()
                .user(question)
                .call()
                .chatClientResponse();

        List<Document> retrieved =
                (List<Document>) resp.context().get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT);


        List<Map<String,Object>> citations = new ArrayList<>();
        if (retrieved != null) {
            int i = 1;
            for (Document d : retrieved) {
                var m = d.getMetadata();
                citations.add(Map.of(
                        "n", i++,
                        "title", m.getOrDefault("file_name","Policy"),
                        "snippet", truncate(d.getText(), 200)
                ));
            }
        }

        return Map.of("answer", resp.chatResponse().getResult().getOutput().getText(), "citations", citations);
    }

    private static String truncate(String s, int n) {
        return (s.length() <= n) ? s : s.substring(0, n - 1) + "…";
    }
}
