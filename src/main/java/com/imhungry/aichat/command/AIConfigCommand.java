package com.imhungry.aichat.command;

import com.imhungry.aichat.config.AIChatConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class AIConfigCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("aiconfig")
                .executes(AIConfigCommand::promptApiKey)
                .then(ClientCommands.literal("key")
                        .then(ClientCommands.argument("apikey", StringArgumentType.greedyString())
                                .executes(AIConfigCommand::setKeyDirect)))
                .then(ClientCommands.literal("model")
                        .then(ClientCommands.argument("model", StringArgumentType.greedyString())
                                .executes(AIConfigCommand::setModel)))
                .then(ClientCommands.literal("system")
                        .then(ClientCommands.argument("prompt", StringArgumentType.greedyString())
                                .executes(AIConfigCommand::setSystemPrompt)))
                .then(ClientCommands.literal("status")
                        .executes(AIConfigCommand::showStatus))
                .then(ClientCommands.literal("reset")
                        .executes(AIConfigCommand::resetKey))
        );
    }

    private static int promptApiKey(CommandContext<FabricClientCommandSource> ctx) {
        AIChatConfig.get().setAwaitingApiKey(true);
        msg(ctx, "§e[AIChat] Paste your OpenRouter API key in chat (won't be sent to the server):");
        msg(ctx, "§7Get one at: https://openrouter.ai/keys");
        return 1;
    }

    private static int setKeyDirect(CommandContext<FabricClientCommandSource> ctx) {
        String key = StringArgumentType.getString(ctx, "apikey").trim();
        AIChatConfig cfg = AIChatConfig.get();
        cfg.apiKey = key;
        cfg.setAwaitingApiKey(false);
        AIChatConfig.save();
        msg(ctx, "§a[AIChat] API key saved!");
        return 1;
    }

    private static int setModel(CommandContext<FabricClientCommandSource> ctx) {
        String model = StringArgumentType.getString(ctx, "model").trim();
        AIChatConfig.get().model = model;
        AIChatConfig.save();
        msg(ctx, "§a[AIChat] Model set to: §f" + model);
        return 1;
    }

    private static int setSystemPrompt(CommandContext<FabricClientCommandSource> ctx) {
        String prompt = StringArgumentType.getString(ctx, "prompt").trim();
        AIChatConfig.get().systemPrompt = prompt;
        AIChatConfig.save();
        msg(ctx, "§a[AIChat] System prompt updated.");
        return 1;
    }

    private static int showStatus(CommandContext<FabricClientCommandSource> ctx) {
        AIChatConfig cfg = AIChatConfig.get();
        msg(ctx, "§6=== AIChat Status ===");
        msg(ctx, "§7API Key: " + (cfg.isConfigured() ? "§a✔ Set" : "§c✘ Not set"));
        msg(ctx, "§7Model: §f" + cfg.model);
        msg(ctx, "§7AI: " + (cfg.enabled ? "§aON" : "§cOFF"));
        msg(ctx, "§7System: §f" + cfg.systemPrompt);
        return 1;
    }

    private static int resetKey(CommandContext<FabricClientCommandSource> ctx) {
        AIChatConfig.get().apiKey = "";
        AIChatConfig.save();
        msg(ctx, "§c[AIChat] API key cleared.");
        return 1;
    }

    private static void msg(CommandContext<FabricClientCommandSource> ctx, String text) {
        ctx.getSource().sendFeedback(Component.literal(text));
    }
}
