package com.soradgaming.simplehudenhanced.mixin;

import com.soradgaming.simplehudenhanced.SimpleHudEnhanced;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.utli.LegacyTexturedButtonWidget;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public class ConfigButton {
    @Inject(method = "createPauseMenu", at = @At("TAIL"))
    private void addCustomButton(CallbackInfo ci) {
        // Check if modmenu is installed and enabled
        if (SimpleHudEnhanced.isModMenuInstalled()) {
            return;
        }

        // Reference to the existing screen
        Screen screen = (Screen) (Object) this;

        // Adding the custom button to the screen
        ((ScreenInvoker) screen).simplehud$addRenderableWidget(new LegacyTexturedButtonWidget(
                screen.width / 2 + 4 + 100 + 2,
                screen.height / 4 + 72 - 16,
                20,
                20,
                0,
                0,
                20,
                ResourceLocation.fromNamespaceAndPath("simplehudenhanced", "textures/mods_button.png"),
                32,
                64,
                button -> Minecraft.getInstance().setScreen(AutoConfig.getConfigScreen(SimpleHudEnhancedConfig.class, screen).get()),
                CommonComponents.EMPTY
        ));
    }
}

