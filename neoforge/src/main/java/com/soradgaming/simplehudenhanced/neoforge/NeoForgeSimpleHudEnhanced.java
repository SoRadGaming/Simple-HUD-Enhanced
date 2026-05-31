package com.soradgaming.simplehudenhanced.neoforge;

import com.soradgaming.simplehudenhanced.Constants;
import com.soradgaming.simplehudenhanced.SimpleHudEnhanced;
import com.soradgaming.simplehudenhanced.client.SimpleHudEnhancedClient;
import com.soradgaming.simplehudenhanced.neoforge.client.NeoForgeSimpleHudEnhancedClient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
public class NeoForgeSimpleHudEnhanced {
    public NeoForgeSimpleHudEnhanced(IEventBus eventBus) {
        SimpleHudEnhanced.init();

        if (FMLEnvironment.dist.isClient()) {
            SimpleHudEnhancedClient.init();
            eventBus.addListener(NeoForgeSimpleHudEnhancedClient::registerKeyMappings);
            NeoForge.EVENT_BUS.addListener(NeoForgeSimpleHudEnhancedClient::onClientTick);
        }
    }
}
