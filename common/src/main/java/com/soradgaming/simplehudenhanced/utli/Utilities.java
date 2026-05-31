package com.soradgaming.simplehudenhanced.utli;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

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
    public static Component translatable(String key) {
        return Component.translatable(key);
    }

    // Get Players Biome
    public static String getBiome(ClientLevel world, LocalPlayer player, boolean toggleBiomeLabel) {
        Optional<ResourceKey<Biome>> biome = world.getBiome(player.blockPosition()).unwrapKey();

        if (biome.isPresent()) {
            String biomeName = Component.translatable("biome." + biome.get().location().getNamespace() + "." + biome.get().location().getPath()).getString();
            if (toggleBiomeLabel) {
                return String.format(Component.translatable("text.hud.simplehudenhanced.biome").getString() + ": %s", Utilities.capitalise(biomeName));
            } else {
                return String.format("%s " + Component.translatable("text.hud.simplehudenhanced.biome").getString() , Utilities.capitalise(biomeName));
            }
        }

        return "";
    }

    //Get Player FPS
    public static String getFPS(Minecraft client) {
        return String.format("%d fps", client.getFps());
    }
}
