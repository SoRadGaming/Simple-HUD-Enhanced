package com.soradgaming.simplehudenhanced.client;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class SimpleHudEnhancedClient implements ClientModInitializer {
    private ConfigHolder<SimpleHudEnhancedConfig> configHolder;

    @Override
    public void onInitializeClient() {
        System.out.println("Simple Hud Enhanced Mod started.");

        this.configHolder = AutoConfig.register(SimpleHudEnhancedConfig.class, Toml4jConfigSerializer::new);

        this.registerKeybindings();
    }

    void registerKeybindings() {
        KeyBinding toggleHudKey = new KeyBinding(
                "key.simplehudenhanced.toggle_hud",
                GLFW.GLFW_KEY_GRAVE_ACCENT, // ` key
                "key.category.simplehudenhanced.hud"
        );

        KeyBinding toggleHudKeybinding = KeyBindingHelper.registerKeyBinding(toggleHudKey);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (toggleHudKeybinding.wasPressed()) {
                SimpleHudEnhancedConfig config = this.configHolder.getConfig();

                String chatMessage = "key.simplehudenhanced.toggle_hud.chat_message.on";
                if (config.uiConfig.toggleSimpleHUDEnhanced) {
                    chatMessage = "key.simplehudenhanced.toggle_hud.chat_message.off";
                }

                client.player.sendMessage(Text.translatable(chatMessage), true);
                config.uiConfig.toggleSimpleHUDEnhanced = !config.uiConfig.toggleSimpleHUDEnhanced;
                // read file and save to file instead of using this method to save config file modifications
                AutoConfig.getConfigHolder(SimpleHudEnhancedConfig.class).save();
            }
        });
    }
}
