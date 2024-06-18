package com.javarush.telegram;

import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatGPTService {
    private ChatGPT chatGPT;

    private List<Message> messageHistory = new ArrayList<>(); //ChatGPT conversation history - needed for dialogues

    public ChatGPTService(String token) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("18.199.183.77", 49232));
        if (token.startsWith("gpt:")) {
            token = "sk-proj-" + new StringBuilder(token.substring(4)).reverse();
        }

        this.chatGPT = ChatGPT.builder()
                .apiKey(token)
                .apiHost("https://api.openai.com/")
                .proxy(proxy)
                .build()
                .init();
    }

    /**
     * Single request to ChatGPT in the format "request" -> "response".
     * The request consists of two parts:
     *      prompt - context of the question
     *      question - the query itself
     */
    public String sendMessage(String prompt, String question) {
        Message system = Message.ofSystem(prompt);
        Message message = Message.of(question);
        messageHistory = new ArrayList<>(Arrays.asList(system, message));

        return sendMessagesToChatGPT();
    }

    /**
     * Requests to ChatGPT with message history retention.
     * The setPrompt() method sets the context of the request
     */
    public void setPrompt(String prompt) {
        Message system = Message.ofSystem(prompt);
        messageHistory = new ArrayList<>(List.of(system));
    }

    /**
     * Requests to ChatGPT with message history retention.
     * The addMessage() method adds a new question (message) to the chat.
     */
    public String addMessage(String question) {
        Message message = Message.of(question);
        messageHistory.add(message);

        return sendMessagesToChatGPT();
    }

    /**
     * Send a series of messages to ChatGPT: prompt, message1, answer1, message2, answer2, ..., messageN
     * The ChatGPT response is added to the end of messageHistory for subsequent use
     */
    private String sendMessagesToChatGPT(){
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName()) // GPT4Turbo or GPT_3_5_TURBO
                .messages(messageHistory)
                .maxTokens(3000)
                .temperature(0.9)
                .build();

        ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
        Message res = response.getChoices().get(0).getMessage();
        messageHistory.add(res);

        return res.getContent();
    }
}

