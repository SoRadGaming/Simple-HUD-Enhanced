package com.soradgaming.simplehudenhanced.platform.services;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.hud.EquipmentInfoStack;
import java.util.List;
import net.minecraft.world.entity.player.Player;

public interface IPlatformHelper {
    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    List<EquipmentInfoStack> getEquipmentInfo(Player player, SimpleHudEnhancedConfig config);
}
