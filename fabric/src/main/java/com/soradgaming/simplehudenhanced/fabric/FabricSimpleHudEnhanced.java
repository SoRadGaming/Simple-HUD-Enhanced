package com.soradgaming.simplehudenhanced.fabric;

import com.soradgaming.simplehudenhanced.SimpleHudEnhanced;
import net.fabricmc.api.ModInitializer;

public class FabricSimpleHudEnhanced implements ModInitializer {
    @Override
    public void onInitialize() {
        SimpleHudEnhanced.init();
    }
}
