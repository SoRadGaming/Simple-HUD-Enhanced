package com.soradgaming.simplehudenhanced.client;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.hud.HUD;
import com.soradgaming.simplehudenhanced.utli.Utilities;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class SimpleHudEnhancedClient implements ClientModInitializer {
    private ConfigHolder<SimpleHudEnhancedConfig> configHolder;
    private HUD hud;

    @Override
    public void onInitializeClient() {
        // Register the config holder for SimpleHudEnhancedConfig
        this.configHolder = AutoConfig.register(SimpleHudEnhancedConfig.class, Toml4jConfigSerializer::new);
        // Register the keybindings
        this.registerKeybindings();
        // Initialize the HUD instance
        HudElementRegistry.attachElementBefore(VanillaHudElements.HELD_ITEM_TOOLTIP ,Identifier.of("simplehudenhanced", "hud"), (context, tickCounter) -> {
            if (!MinecraftClient.getInstance().getDebugHud().shouldShowDebugHud()) {
                if (this.hud == null) {
                    this.hud = HUD.getInstance();
                    // Render the HUD on next tick
                } else {
                    this.hud.drawHud(context);
                }
            }
        });
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

                client.player.sendMessage(Utilities.translatable(chatMessage), true);
                config.uiConfig.toggleSimpleHUDEnhanced = !config.uiConfig.toggleSimpleHUDEnhanced;
                // read file and save to file instead of using this method to save config file modifications
                AutoConfig.getConfigHolder(SimpleHudEnhancedConfig.class).save();
            }
        });
    }
}
