package com.soradgaming.simplehudenhanced.neoforge.platform;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.hud.EquipmentInfoStack;
import com.soradgaming.simplehudenhanced.platform.services.IPlatformHelper;
import com.soradgaming.simplehudenhanced.utli.VanillaEquipmentHelper;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public List<EquipmentInfoStack> getEquipmentInfo(Player player, SimpleHudEnhancedConfig config) {
        return VanillaEquipmentHelper.getEquipmentInfo(player);
    }
}
