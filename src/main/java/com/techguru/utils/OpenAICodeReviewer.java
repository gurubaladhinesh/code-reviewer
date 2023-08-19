package com.techguru.utils;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

public class OpenAICodeReviewer {

    private static final String OPEN_AI_API_KEY = "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";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);

    public static String getResponse(OpenAiService service, String prompt) {
        List<ChatMessage> messages = new LinkedList<>();
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);
        messages.add(userMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .build();
        ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
        return responseMessage.getContent();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide a file path.");
            System.exit(1);
        }
        String filePath = args[0];
        OpenAiService service = new OpenAiService(OPEN_AI_API_KEY, DEFAULT_TIMEOUT);


        String reset = "\u001B[0m";
        String green = "\u001B[32m";

        try {
            // Read the file and get the code.
            String code = new String(Files.readAllBytes(Paths.get(filePath)));

            // Build the prompt for OpenAI API.
            String prompt = String.format(
                    "Review the code below and provide feedback on how to improve it.%n%n%s%n",
                    code
            );

            if (prompt == null) {
                System.out.println("Please provide a valid command.");
                System.exit(1);
            }

            // Get a response from OpenAI API.
            String response = getResponse(service, prompt);

            System.out.printf("%sReview %s:%s%n%s%s%n",
                    green, filePath, reset, response, reset);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
