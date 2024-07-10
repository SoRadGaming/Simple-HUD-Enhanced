package com.soradgaming.simplehudenhanced.mixin;

import com.soradgaming.simplehudenhanced.SimpleHudEnhanced;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class ConfigButton {
    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void addCustomButton(CallbackInfo ci) {
        // Check if modmenu is installed and enabled
        if (SimpleHudEnhanced.isModMenuInstalled()) {
            return;
        }

        // Reference to the existing screen
        Screen screen = (Screen) (Object) this;

        // Adding the custom button to the screen
        ((ScreenInvoker) screen).invokeAddDrawableChild(new TexturedButtonWidget(
                screen.width / 2 + 4 + 100 + 2,
                screen.height / 4 + 72 - 16,
                20,
                20,
                0,
                0,
                20,
                new Identifier("simplehudenhanced", "textures/mods_button.png"),
                32,
                64,
                button -> MinecraftClient.getInstance().setScreen(AutoConfig.getConfigScreen(SimpleHudEnhancedConfig.class, screen).get()),
                LiteralText.EMPTY
        ));
    }
}

