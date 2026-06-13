package com.imhungry.aichat.api;

import com.google.gson.*;
import com.imhungry.aichat.AIChatMod;
import com.imhungry.aichat.config.AIChatConfig;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class OpenRouterClient {

    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    private static final Gson GSON = new Gson();

    /**
     * Sends a chat message asynchronously. Calls onReply with the reply text,
     * or onError with an error message.
     */
    public static CompletableFuture<Void> sendMessage(
            List<ChatMessage> history,
            Consumer<String> onReply,
            Consumer<String> onError) {

        AIChatConfig cfg = AIChatConfig.get();

        JsonObject body = new JsonObject();
        body.addProperty("model", cfg.model);
        body.addProperty("max_tokens", 256);

        JsonArray messages = new JsonArray();

        // System prompt
        JsonObject sys = new JsonObject();
        sys.addProperty("role", "system");
        sys.addProperty("content", cfg.systemPrompt);
        messages.add(sys);

        // Conversation history
        for (ChatMessage msg : history) {
            JsonObject m = new JsonObject();
            m.addProperty("role", msg.role());
            m.addProperty("content", msg.content());
            messages.add(m);
        }

        body.add("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + cfg.apiKey)
                .header("HTTP-Referer", "https://github.com/alamien060512-alt")
                .header("X-Title", "Minecraft AIChat Mod")
                .POST(HttpRequest.BodyPublishers.ofString(
                        GSON.toJson(body), StandardCharsets.UTF_8))
                .build();

        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    try {
                        if (response.statusCode() != 200) {
                            onError.accept("API error " + response.statusCode() + ": " + extractError(response.body()));
                            return;
                        }
                        String reply = extractReply(response.body());
                        onReply.accept(reply);
                    } catch (Exception e) {
                        onError.accept("Parse error: " + e.getMessage());
                    }
                })
                .exceptionally(ex -> {
                    onError.accept("Network error: " + ex.getMessage());
                    return null;
                });
    }

    private static String extractReply(String json) {
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        return obj.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString()
                .trim();
    }

    private static String extractError(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            if (obj.has("error")) {
                JsonElement err = obj.get("error");
                if (err.isJsonObject()) return err.getAsJsonObject().get("message").getAsString();
                return err.getAsString();
            }
        } catch (Exception ignored) {}
        return json.length() > 120 ? json.substring(0, 120) : json;
    }

    public record ChatMessage(String role, String content) {}
}
