package com.soradgaming.simplehudenhanced.fabric.platform;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.hud.EquipmentInfoStack;
import com.soradgaming.simplehudenhanced.platform.services.IPlatformHelper;
import com.soradgaming.simplehudenhanced.utli.VanillaEquipmentHelper;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public List<EquipmentInfoStack> getEquipmentInfo(Player player, SimpleHudEnhancedConfig config) {
        if (isModLoaded("trinkets")) {
            return FabricTrinketsCompat.getEquipmentInfo(player, config);
        }

        return VanillaEquipmentHelper.getEquipmentInfo(player);
    }
}
