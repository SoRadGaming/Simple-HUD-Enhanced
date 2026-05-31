package com.soradgaming.simplehudenhanced;

import com.soradgaming.simplehudenhanced.platform.Services;

public final class SimpleHudEnhanced {
    public static final String MOD_ID = "simplehudenhanced";

    private SimpleHudEnhanced() {
    }

    public static boolean isModMenuInstalled() {
        return Services.PLATFORM.isModLoaded("modmenu");
    }

    public static boolean isTrinketsInstalled() {
        return Services.PLATFORM.isModLoaded("trinkets");
    }

    public static void init() {
        // Check if Trinket mod is installed
        if (isTrinketsInstalled()) {
            Constants.LOG.info("Trinket mod is installed. Adding compatibility features.");
        } else {
            Constants.LOG.info("Trinket mod is not installed. Skipping compatibility features.");
        }

        // Check if ModMenu is installed
        if (isModMenuInstalled()) {
            Constants.LOG.info("ModMenu is installed. Adding compatibility features.");
        } else {
            Constants.LOG.info("ModMenu is not installed. Injecting custom config button.");
        }

        Constants.LOG.info("Simple Hud Enhanced Mod started on {}.", Services.PLATFORM.getPlatformName());
    }
}
