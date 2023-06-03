package com.soradgaming.simplehudenhanced.utli;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.biome.Biome;

import java.util.Optional;

public class Utilities {
    public static String getModName() {
        return "Simple HUD Enhanced";
    }

    public static String capitalise(String str) {
        // Capitalise first letter of a String
        if (str == null) return null;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**********************************************************
     Mod Version Management - Separate for Version Management
     **********************************************************/

    // Text Management
    public static Text translatable(String key) {
        return Text.translatable(key);
    }

    // Get Players Biome
    public static String getBiome(ClientWorld world, ClientPlayerEntity player) {
        Optional<RegistryKey<Biome>> biome = world.getBiome(player.getBlockPos()).getKey();

        if (biome.isPresent()) {
            String biomeName = Text.translatable("biome." + biome.get().getValue().getNamespace() + "." + biome.get().getValue().getPath()).getString();
            return String.format("%s " + Text.translatable("text.hud.simplehudenhanced.biome").getString() , Utilities.capitalise(biomeName));
        }

        return "";
    }

    //Get Player FPS
    public static String getFPS(MinecraftClient client) {
        return String.format("%d fps", client.getCurrentFps());
    }
}
