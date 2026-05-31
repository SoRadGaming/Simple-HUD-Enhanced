package com.soradgaming.simplehudenhanced.fabric.client;

import com.soradgaming.simplehudenhanced.client.SimpleHudEnhancedClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class FabricSimpleHudEnhancedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SimpleHudEnhancedClient.init();
        registerKeybindings();
    }

    private void registerKeybindings() {
        KeyMapping toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.simplehudenhanced.toggle_hud",
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                "key.category.simplehudenhanced.hud"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKey.consumeClick()) {
                SimpleHudEnhancedClient.toggleHud(client);
            }
        });
    }
}
