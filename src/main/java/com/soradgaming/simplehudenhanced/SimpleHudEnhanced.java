package com.soradgaming.simplehudenhanced;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class SimpleHudEnhanced implements ModInitializer {
    public static boolean isModMenuInstalled() {
        return FabricLoader.getInstance().isModLoaded("modmenu");
    }
    public static boolean isTrinketsInstalled() {
        return FabricLoader.getInstance().isModLoaded("trinkets");
    }

    @Override
    public void onInitialize() {
        // Check if Trinket mod is installed
        if (isTrinketsInstalled()) {
            System.out.println("Trinket mod is installed! Adding compatibility features...");
            System.out.println("Disabling Render Cache for Trinket mod compatibility...");
        } else {
            System.out.println("Trinket mod is not installed. Skipping compatibility features...");
        }

        // Check if ModMenu is installed
        if (isModMenuInstalled()) {
            System.out.println("ModMenu is installed! Adding compatibility features...");
        } else {
            System.out.println("ModMenu is not installed. Skipping compatibility features...");
            System.out.println("Injecting Custom Config Button...");
        }

        System.out.println("Simple Hud Enhanced Mod started.");
    }
}
