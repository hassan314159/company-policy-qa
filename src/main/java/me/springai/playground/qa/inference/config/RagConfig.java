package me.springai.playground.qa.inference.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class RagConfig {

    @Bean
    public VectorStoreDocumentRetriever policyRetriever(
            VectorStore vectorStore,
            @Value("${rag.retriever.top-k:8}") int topK,
            @Value("${rag.retriever.similarity-threshold:0.50}") double similarityThreshold
    ) {
        return VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .topK(topK)
                .similarityThreshold(similarityThreshold)
                .build();
    }


    @Bean
    public RetrievalAugmentationAdvisor ragAdvisor(VectorStoreDocumentRetriever policyRetriever) {
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(policyRetriever)
                .build();
    }


    @Bean
    public ChatClient chatClient(
            ChatModel chatModel,
            RetrievalAugmentationAdvisor ragAdvisor,
            @Value("classpath:prompts/qa-policy-system.st") Resource systemTemplate
    ) {
        return ChatClient.builder(chatModel)
                .defaultSystem(systemTemplate)
                .defaultAdvisors(ragAdvisor)
                .defaultOptions(ChatOptions
                        .builder()
                        .temperature(0.3d)
                        .build())
                .build();
    }
}
