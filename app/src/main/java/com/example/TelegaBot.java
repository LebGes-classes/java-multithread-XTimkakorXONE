package com.example;

import java.io.*;
import java.net.*;
import org.json.*;
import java.util.List;
import java.util.ArrayList;

public class TelegaBot {
    private static final String TOKEN_BOT = "7918983194:AAH5JMtihcT2TY6Mlex0tqVOYyM2qUTN60Q";
    private static int lastUpdateId = 0;
    private static boolean botStarted = false;
    private static List<Worker> workers = new ArrayList<>();
    private static boolean isRunning = true;
    public static void setWorkers(List<Worker> workerList) {
        workers = workerList;
    }

    public static void main(String[] args) {
        System.out.println("Telegram бот запущен...");
        while (isRunning) { 
            try {
                JSONArray updates = getUpdates();
                for (int i = 0; i < updates.length(); i++) {
                    JSONObject update = updates.getJSONObject(i);
                    int updateId = update.getInt("update_id");

                    if (updateId > lastUpdateId) {
                        lastUpdateId = updateId;
                        JSONObject message = update.getJSONObject("message");
                        String text = message.getString("text");
                        var chatId = message.getJSONObject("chat").getLong("id");

                        if ("/start".equalsIgnoreCase(text)) {
                            botStarted = true;
                            sendMessage(chatId, "Бот активирован! Теперь я буду отвечать на ваши сообщения.");
                            System.out.println("Бот активирован пользователем: " + chatId);
                        }
                        
                        if (botStarted) {
                            if ("/status".equalsIgnoreCase(text)) {
                                StringBuilder status = new StringBuilder("Статус работников:\n");
                                for (Worker worker : workers) {
                                    status.append(worker.toString()).append("\n");
                                }
                                sendMessage(chatId, status.toString());
                            } else if (!text.startsWith("/")) {
                                sendMessage(chatId, "Есть команды:\n/status - показать статус работников\n/end - завершить работу бота! ");
                            }

                            else if ("/end".equalsIgnoreCase(text)) {
                            botStarted = false;
                            isRunning = false;
                            sendMessage(chatId, "Бот закончил работу! ");
                        }

                        else if ("/help".equalsIgnoreCase(text)) {
                            sendMessage(chatId, "Есть команды:\n/status - показать статус работников\n/end - завершить работу бота! ");
                            System.out.println("Бот слился");
                        }
                        }
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                System.err.println("Ошибка в боте: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static JSONArray getUpdates() throws Exception {
        String url = String.format("https://api.telegram.org/bot%s/getUpdates?offset=%d", TOKEN_BOT, lastUpdateId + 1);
        String response = sendGetRequest(url);
        JSONObject jsonResponse = new JSONObject(response);
        if (!jsonResponse.getBoolean("ok")) {
            throw new Exception("Ошибка API Telegram: " + jsonResponse.optString("description", "Неизвестная ошибка"));
        }
        return jsonResponse.getJSONArray("result");
    }

    private static void sendMessage(long chatId, String text) throws Exception {
        String url = String.format("https://api.telegram.org/bot%s/sendMessage", TOKEN_BOT);
        String params = String.format("chat_id=%d&text=%s", chatId, URLEncoder.encode(text, "UTF-8"));
        String response = sendPostRequest(url, params);
        JSONObject jsonResponse = new JSONObject(response);
        if (!jsonResponse.getBoolean("ok")) {
            throw new Exception("Ошибка отправки сообщения: " + jsonResponse.optString("description", "Неизвестная ошибка"));
        }
    }

    private static String sendGetRequest(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } 
    }

    private static String sendPostRequest(String url, String params) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = params.getBytes("UTF-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}
