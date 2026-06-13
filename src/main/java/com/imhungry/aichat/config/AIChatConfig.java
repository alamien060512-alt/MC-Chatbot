package com.imhungry.aichat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imhungry.aichat.AIChatMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class AIChatConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("aichat.json");

    private static AIChatConfig instance;

    public String apiKey = "";
    public String model = "openai/gpt-4o-mini";
    public boolean enabled = true;
    public String systemPrompt = "You are a helpful assistant inside Minecraft. Keep replies short (1-2 sentences).";

    private transient boolean awaitingApiKey = false;

    public static AIChatConfig get() {
        if (instance == null) load();
        return instance;
    }

    public boolean isAwaitingApiKey() { return awaitingApiKey; }
    public void setAwaitingApiKey(boolean v) { awaitingApiKey = v; }
    public boolean isConfigured() { return apiKey != null && !apiKey.isBlank(); }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader r = Files.newBufferedReader(CONFIG_PATH)) {
                instance = GSON.fromJson(r, AIChatConfig.class);
                if (instance == null) instance = new AIChatConfig();
            } catch (IOException e) {
                AIChatMod.LOGGER.error("[AIChat] Failed to load config", e);
                instance = new AIChatConfig();
            }
        } else {
            instance = new AIChatConfig();
        }
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(instance, w);
        } catch (IOException e) {
            AIChatMod.LOGGER.error("[AIChat] Failed to save config", e);
        }
    }
}
