package me.springai.playground.qa.inference.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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


        List<Map<String,Object>> citations = toCitations(retrieved);

        return Map.of("answer", resp.chatResponse().getResult().getOutput().getText(), "citations", citations);
    }

    public Flux<Map<String, Object>> answerStream(String question) {
        AtomicReference<List<Document>> docsRef = new AtomicReference<>(null);

        Flux<ChatClientResponse> base = chatClient
                .prompt()
                .user(question)
                .stream()
                .chatClientResponse()
                .doOnNext(cr -> {
                    if (docsRef.get() == null) {
                        var docs = (List<Document>) cr.context().get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT);
                        if (docs != null) docsRef.compareAndSet(null, docs);
                    }
                })
                .share();

        Flux<Map<String,Object>> tokens = base
                .map(cr -> cr.chatResponse().getResult().getOutput().getText())
                .filter(s -> s != null && !s.isEmpty())
                .map(s -> Map.of("type", "token", "data", s));

        Mono<Map<String,Object>> citations = Mono.defer(() ->
                Mono.just(Map.of("type", "citations",
                        "data", toCitations(Optional.ofNullable(docsRef.get()).orElse(List.of())))));

        return Flux.concat(tokens, citations);
    }



    private List<Map<String, Object>> toCitations(List<Document> docs) {
        List<Map<String, Object>> out = new ArrayList<>();
        int i = 1;
        for (Document d : docs) {
            var m = d.getMetadata();
            out.add(Map.of(
                    "n", i++,
                    "title", m.getOrDefault("file_name", "Policy"),
                    "page", m.getOrDefault("page_number", -1),
                    "snippet", truncate(d.getText(), 200)
            ));
        }
        return out;
    }

    private static String truncate(String s, int n) {
        s = s.replaceAll("\\s+"," ");
        return (s.length() <= n) ? s : s.substring(0, n - 1) + "…";
    }
}
