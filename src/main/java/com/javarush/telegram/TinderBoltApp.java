package com.javarush.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.InputStream;
import java.util.Properties;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME;
    public static final String TELEGRAM_BOT_TOKEN;
    public static final String OPEN_AI_TOKEN;

    static {
        Properties properties = new Properties();
        try (InputStream input = TinderBoltApp.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                throw new RuntimeException("Configuration file not found");
            }
            properties.load(input);
            TELEGRAM_BOT_NAME = properties.getProperty("TELEGRAM_BOT_NAME");
            TELEGRAM_BOT_TOKEN = properties.getProperty("TELEGRAM_BOT_TOKEN");
            OPEN_AI_TOKEN = properties.getProperty("OPEN_AI_TOKEN");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load properties file", ex);
        }
    }

    private ChatGPTService chatGPT = new ChatGPTService(OPEN_AI_TOKEN);
    private DialogMode currentMode = null;

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        // TODO: the main functionality of the bot will be written here
        String message = getMessageText();
        if (message.equals("/start") || message.equals("/Start"))
        {
            currentMode = DialogMode.MAIN;
            sendPhotoMessage("bot_avatar");
            String text = loadMessage("main");
            sendTextMessage(text);
            showMainMenu("Start", "/start",
                    "Profile", "/profile",
                    "Opener", "/opener",
                    "Message", "/message",
                    "Date", "/date",
                    "ChatGPT", "/gpt");
            return;
        }

        if(message.equals("/gpt") || message.equals("/Gpt"))
        {
            currentMode = DialogMode.GPT;
            sendPhotoMessage("gpt");
            sendTextMessage("Type your message to *ChatGPT*: ");
            return;
        }

        if (currentMode == DialogMode.GPT)
        {
            String prompt = loadPrompt("gpt");
            String answer = chatGPT.sendMessage(prompt, message);
            sendTextMessage(answer);
            return;
        }

        sendTextMessage("*Hi!*");
        sendTextMessage("_You have sent:_ " + message);
        sendTextButtonsMessage("Please, select mode:",
                "Mode 1", "mode1_key",
                "Mode 2", "mode2_key");
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}








