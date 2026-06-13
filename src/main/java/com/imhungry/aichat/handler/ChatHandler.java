package com.imhungry.aichat.handler;

import com.imhungry.aichat.api.OpenRouterClient;
import com.imhungry.aichat.api.OpenRouterClient.ChatMessage;
import com.imhungry.aichat.config.AIChatConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ChatHandler {

    private static final int MAX_HISTORY = 20;
    private static final List<ChatMessage> history = new ArrayList<>();

    public static boolean onChatMessage(String message) {
        AIChatConfig cfg = AIChatConfig.get();

        if (cfg.isAwaitingApiKey()) {
            cfg.setAwaitingApiKey(false);
            String key = message.trim();
            if (key.isBlank()) {
                sendLocal("§c[AIChat] No key entered. Run /aiconfig again.");
            } else {
                cfg.apiKey = key;
                AIChatConfig.save();
                sendLocal("§a[AIChat] API key saved! Type normally to chat with AI.");
            }
            return false;
        }

        if (!cfg.enabled || !cfg.isConfigured()) {
            return true;
        }

        String userMsg = message.trim();
        sendLocal("§7[You → AI] " + userMsg);

        history.add(new ChatMessage("user", userMsg));
        if (history.size() > MAX_HISTORY) history.remove(0);

        List<ChatMessage> snapshot = new ArrayList<>(history);

        OpenRouterClient.sendMessage(
                snapshot,
                reply -> {
                    history.add(new ChatMessage("assistant", reply));
                    if (history.size() > MAX_HISTORY) history.remove(0);
                    Minecraft.getInstance().execute(() -> sendLocal("§b[MC-Chatbot] §f" + reply));
                },
                error -> Minecraft.getInstance().execute(() -> sendLocal("§c[MC-Chatbot Error] " + error))
        );

        return false;
    }

    public static void clearHistory() {
        history.clear();
    }

    private static void sendLocal(String text) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(Component.literal(text));
        }
    }
}
