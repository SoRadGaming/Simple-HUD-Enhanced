package com.soradgaming.simplehudenhanced.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import com.soradgaming.simplehudenhanced.utli.Colours;
import net.minecraft.entity.effect.StatusEffectInstance;

@Config(name = "simplehudenhanced")
public class SimpleHudEnhancedConfig implements ConfigData {
    @ConfigEntry.Category("Status Elements")
    @ConfigEntry.Gui.TransitiveObject
    public UIConfig uiConfig = new UIConfig();
    @ConfigEntry.Category("Status Elements")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public StatusElements statusElements = new StatusElements();

    @ConfigEntry.Category("Effects Status")
    @ConfigEntry.Gui.Tooltip
    public boolean toggleEffectsStatus = true;
    @ConfigEntry.Category("Effects Status")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip(count = 4)
    public ColorMode colorMode = ColorMode.EFFECT_COLOR;
    @ConfigEntry.Category("Effects Status")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public EffectsStatus effectsStatus = new EffectsStatus();

    @ConfigEntry.Category("Movement Status")
    @ConfigEntry.Gui.Tooltip
    public boolean toggleMovementStatus = false;

    @ConfigEntry.Category("Movement Status")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public MovementElements movementStatus = new MovementElements();

    @ConfigEntry.Category("Equipment Status")
    @ConfigEntry.Gui.Tooltip
    public boolean toggleEquipmentStatus = false;

    @ConfigEntry.Category("Equipment Status")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public EquipmentStatus equipmentStatus = new EquipmentStatus();

    public static class UIConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean toggleSimpleHUDEnhanced = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker
        public int textColor = Colours.WHITE;
    }

    public static class EffectsStatus {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker(allowAlpha = true)
        public int backgroundColor = 0x80000000;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker(allowAlpha = true)
        public int beneficialForegroundColor = 0x80ffffff;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker(allowAlpha = true)
        public int harmfulForegroundColor = beneficialForegroundColor;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.ColorPicker(allowAlpha = true)
        public int neutralForegroundColor = beneficialForegroundColor;
    }
    public int getColor(StatusEffectInstance effect) {
        if (this.colorMode == null) return Colours.WHITE;
        return this.colorMode.getColor(this, effect);
    }

    public static class EquipmentStatus {
        @ConfigEntry.Gui.Tooltip
        public boolean showCount = true;
        @ConfigEntry.Gui.Tooltip
        public boolean showDurability = true;
        @ConfigEntry.Gui.Tooltip
        public boolean showNonTools = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int equipmentStatusLocationX = 0;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int equipmentStatusLocationY = 60;
    }

    public static class MovementElements {
        @ConfigEntry.Gui.Tooltip
        public boolean toggleSprintStatus = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleSneakStatus = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleFlyingStatus = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleSwimmingStatus = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int movementStatusLocationX = 0;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int movementStatusLocationY = 90;
    }

    public static class StatusElements {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int Xcords = 0;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int Ycords = 0;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public Coordinates coordinates = new Coordinates();
        @ConfigEntry.Gui.Tooltip
        public boolean toggleFps = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public PlayerSpeed playerSpeed = new PlayerSpeed();
        @ConfigEntry.Gui.Tooltip
        public boolean toggleLightLevel = false;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleBiome = true;
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
        public GameTime gameTime = new GameTime();
        @ConfigEntry.Gui.Tooltip
        public boolean togglePlayerName = false;
        @ConfigEntry.Gui.Tooltip
        public boolean togglePing = false;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleServerName = false;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleServerAddress = false;
    }

    public static class PlayerSpeed {
        @ConfigEntry.Gui.Tooltip
        public boolean togglePlayerSpeed = true;
        @ConfigEntry.Gui.Tooltip
        public boolean togglePlayerVerticalSpeed = false;
    }

    public static class Coordinates {
        @ConfigEntry.Gui.Tooltip
        public boolean toggleCoordinates = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleDirection = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleOffset = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleNetherCoordinateConversion = false;
    }

    public static class GameTime {
        @ConfigEntry.Gui.Tooltip
        public boolean toggleGameTime = true;
        @ConfigEntry.Gui.Tooltip
        public boolean toggleGameTime24Hour = false;
    }
}
