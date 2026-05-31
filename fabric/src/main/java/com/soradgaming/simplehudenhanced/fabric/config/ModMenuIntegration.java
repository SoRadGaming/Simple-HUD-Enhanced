package com.soradgaming.simplehudenhanced.fabric.config;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(SimpleHudEnhancedConfig.class, parent).get();
    }
}
