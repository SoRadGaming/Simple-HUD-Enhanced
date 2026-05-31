package com.soradgaming.simplehudenhanced.neoforge.client;

import com.soradgaming.simplehudenhanced.client.SimpleHudEnhancedClient;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public final class NeoForgeSimpleHudEnhancedClient {
    private static final KeyMapping TOGGLE_HUD_KEY = new KeyMapping(
            "key.simplehudenhanced.toggle_hud",
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            "key.category.simplehudenhanced.hud"
    );

    private NeoForgeSimpleHudEnhancedClient() {
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_HUD_KEY);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        while (TOGGLE_HUD_KEY.consumeClick()) {
            SimpleHudEnhancedClient.toggleHud(client);
        }
    }
}
