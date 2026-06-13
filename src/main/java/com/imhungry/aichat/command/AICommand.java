package com.imhungry.aichat.command;

import com.imhungry.aichat.config.AIChatConfig;
import com.imhungry.aichat.handler.ChatHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class AICommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("ai")
                .then(ClientCommands.literal("on").executes(AICommand::turnOn))
                .then(ClientCommands.literal("off").executes(AICommand::turnOff))
                .then(ClientCommands.literal("clear").executes(AICommand::clearHistory))
                .executes(AICommand::toggle)
        );
    }

    private static int turnOn(CommandContext<FabricClientCommandSource> ctx) {
        AIChatConfig cfg = AIChatConfig.get();
        if (!cfg.isConfigured()) {
            ctx.getSource().sendFeedback(Component.literal("§c[AIChat] No API key set. Run /aiconfig first."));
            return 0;
        }
        cfg.enabled = true;
        AIChatConfig.save();
        ctx.getSource().sendFeedback(Component.literal("§a[AIChat] AI chat is now ON. Messages go to the AI instead of the server."));
        return 1;
    }

    private static int turnOff(CommandContext<FabricClientCommandSource> ctx) {
        AIChatConfig.get().enabled = false;
        AIChatConfig.save();
        ctx.getSource().sendFeedback(Component.literal("§c[AIChat] AI chat is now OFF. Messages go to the server normally."));
        return 1;
    }

    private static int toggle(CommandContext<FabricClientCommandSource> ctx) {
        AIChatConfig cfg = AIChatConfig.get();
        if (!cfg.isConfigured() && !cfg.enabled) {
            ctx.getSource().sendFeedback(Component.literal("§c[AIChat] No API key set. Run /aiconfig first."));
            return 0;
        }
        cfg.enabled = !cfg.enabled;
        AIChatConfig.save();
        ctx.getSource().sendFeedback(Component.literal(cfg.enabled ? "§a[AIChat] AI chat ON." : "§c[AIChat] AI chat OFF."));
        return 1;
    }

    private static int clearHistory(CommandContext<FabricClientCommandSource> ctx) {
        ChatHandler.clearHistory();
        ctx.getSource().sendFeedback(Component.literal("§e[AIChat] Conversation history cleared."));
        return 1;
    }
}
