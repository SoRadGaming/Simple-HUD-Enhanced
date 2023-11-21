package com.soradgaming.simplehudenhanced;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class SimpleHudEnhanced implements ModInitializer {

    @Override
    public void onInitialize() {
        // Check if Trinket mod is installed
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            System.out.println("Trinket mod is installed! Adding compatibility features...");
        } else {
            System.out.println("Trinket mod is not installed. Skipping compatibility features...");
        }
        System.out.println("Simple Hud Enhanced Mod started.");
    }
}
