package com.imhungry.aichat;

import com.imhungry.aichat.command.AICommand;
import com.imhungry.aichat.command.AIConfigCommand;
import com.imhungry.aichat.handler.ChatHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AIChatMod implements ClientModInitializer {

    public static final String MOD_ID = "aichat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("[AIChat] Mod loaded.");

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            AIConfigCommand.register(dispatcher);
            AICommand.register(dispatcher);
        });

        ClientSendMessageEvents.ALLOW_CHAT.register(ChatHandler::onChatMessage);
    }
}
