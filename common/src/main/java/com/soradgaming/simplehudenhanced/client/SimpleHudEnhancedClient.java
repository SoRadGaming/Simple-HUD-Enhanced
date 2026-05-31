package com.soradgaming.simplehudenhanced.client;

import com.soradgaming.simplehudenhanced.Constants;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.utli.Utilities;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.minecraft.client.Minecraft;

public final class SimpleHudEnhancedClient {
    private static ConfigHolder<SimpleHudEnhancedConfig> configHolder;

    private SimpleHudEnhancedClient() {
    }

    public static void init() {
        if (configHolder != null) {
            return;
        }

        configHolder = AutoConfig.register(SimpleHudEnhancedConfig.class, Toml4jConfigSerializer::new);
        Constants.LOG.info("Simple Hud Enhanced client started.");
    }

    public static ConfigHolder<SimpleHudEnhancedConfig> getConfigHolder() {
        init();
        return configHolder;
    }

    public static void toggleHud(Minecraft client) {
        if (client.player == null) {
            return;
        }

        SimpleHudEnhancedConfig config = getConfigHolder().getConfig();

        String chatMessage = "key.simplehudenhanced.toggle_hud.chat_message.on";
        if (config.uiConfig.toggleSimpleHUDEnhanced) {
            chatMessage = "key.simplehudenhanced.toggle_hud.chat_message.off";
        }

        client.player.displayClientMessage(Utilities.translatable(chatMessage), true);
        config.uiConfig.toggleSimpleHUDEnhanced = !config.uiConfig.toggleSimpleHUDEnhanced;
        AutoConfig.getConfigHolder(SimpleHudEnhancedConfig.class).save();
    }
}
