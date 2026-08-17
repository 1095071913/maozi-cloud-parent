package com.maozi.ai.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author pengjinlong
 * @since 2026/8/15 20:12
 */
@Configuration
public class AIChatConfig {

//    @Bean
//    public Advisor loggerAdvisor() {
//        return new SimpleLoggerAdvisor();
//    }

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(200)
                .build();
    }

    @Bean
    public Advisor chatMemoryAdvisor(ChatMemory chatMemory){
        return MessageChatMemoryAdvisor.builder(chatMemory).build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, Advisor chatMemoryAdvisor) {
        return chatClientBuilder
                .defaultAdvisors(chatMemoryAdvisor)
                .build();
    }

}
