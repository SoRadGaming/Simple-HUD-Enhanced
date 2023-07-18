package com.soradgaming.simplehudenhanced.config;

import com.soradgaming.simplehudenhanced.utli.Utilities;

public enum ColorModeSelector {
    EFFECT_COLOR,
    CATEGORY_COLOR,
    CUSTOM;

    // This is the method invoked when the user selects a color mode in the config GUI.
    @Override
    public String toString() {
        return Utilities.translatable("text.autoconfig.simplehudenhanced.option.colorMode." + name()).getString();
    }
}
