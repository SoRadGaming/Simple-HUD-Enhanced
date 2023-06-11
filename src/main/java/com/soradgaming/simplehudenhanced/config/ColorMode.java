package com.soradgaming.simplehudenhanced.config;

import com.soradgaming.simplehudenhanced.utli.Utilities;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.function.ToIntBiFunction;

public enum ColorMode {
    EFFECT_COLOR(
            (config, effect) -> effect.getEffectType().getColor() | 0xff000000
    ),
    @SuppressWarnings("ConstantConditions")
    CATEGORY_COLOR(
            (config, effect) -> effect.getEffectType().getCategory().getFormatting().getColorValue() | 0xff000000
    ),
    CUSTOM(
            (config, effect) -> switch (effect.getEffectType().getCategory()) {
                case BENEFICIAL -> config.effectsStatus.beneficialForegroundColor;
                case HARMFUL -> config.effectsStatus.harmfulForegroundColor;
                default -> config.effectsStatus.neutralForegroundColor;
            }
    );

    private final ToIntBiFunction<SimpleHudEnhancedConfig, StatusEffectInstance> provider;

    ColorMode(ToIntBiFunction<SimpleHudEnhancedConfig, StatusEffectInstance> provider) {
        this.provider = provider;
    }

    public int getColor(SimpleHudEnhancedConfig config, StatusEffectInstance effect) {
        return provider.applyAsInt(config, effect);
    }

    @Override
    public String toString() {
        return Utilities.translatable("text.autoconfig.simplehudenhanced.option.colorMode." + name()).getString();
    }

}
