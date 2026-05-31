package com.soradgaming.simplehudenhanced.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.client.SimpleHudEnhancedClient;
import com.soradgaming.simplehudenhanced.hud.HUD;
import com.soradgaming.simplehudenhanced.hud.StatusEffectBarRenderer;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mixin(value = Gui.class)
public class GameRender {
    @Unique
    private HUD hud;
    @Unique
    private SimpleHudEnhancedConfig config;
    @Shadow
    @Final
    private Minecraft minecraft;
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void onInit(Minecraft client, CallbackInfo ci) {
        SimpleHudEnhancedClient.init();
        // Get Config
        this.config = SimpleHudEnhancedClient.getConfigHolder().getConfig();
        // Register Save Listener
        AutoConfig.getConfigHolder(SimpleHudEnhancedConfig.class).registerSaveListener((manager, data) -> {
            // Update local config when new settings are saved
            this.config = data;

            HUD hud = HUD.getInstance();

            // Update Sprint Timer
            if (hud != null) hud.sprintTimer = data.paperDoll.paperDollTimeOut;

            return InteractionResult.SUCCESS;
        });
        // Start Mixin
        HUD.initialize(client, config);
        this.hud = HUD.getInstance();

        // Start a new thread to update the equipment cache in the background
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            // Update Equipment Cache
            HUD hud = HUD.getInstance();
            if (hud != null && Minecraft.getInstance().player != null) {
                hud.getEquipmentCache().updateCache(Minecraft.getInstance().player);
                hud.getMovementCache().updateCache(Minecraft.getInstance().player);
                hud.getStatusCache().updateCache();
            }
        }, 0, 50, TimeUnit.MILLISECONDS); // 20 times a second TimeUnit.MILLISECONDS

    }

    @Unique private boolean hudHiddenChecked = false;
    @Unique private boolean prevState = false;
    @Unique private void autoHideHud() {
        if (this.minecraft.options.hideGui && !hudHiddenChecked) {
            hudHiddenChecked = true;
            prevState = config.uiConfig.toggleSimpleHUDEnhanced;
            config.uiConfig.toggleSimpleHUDEnhanced = false;
        }

        if (!this.minecraft.options.hideGui && hudHiddenChecked) {
            hudHiddenChecked = false;
            config.uiConfig.toggleSimpleHUDEnhanced = prevState;
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onDraw(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        // Auto hide HUD on F1
        autoHideHud();

        if (!this.minecraft.gui.getDebugOverlay().showDebugScreen()) {
            // Call async rendering
            this.hud.drawHud(context);
        }
    }

    // Injects into the effect icon rendering to draw status effect bars on the HUD.
    @Inject(method = "renderEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/MobEffectTextureManager;get(Lnet/minecraft/core/Holder;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", ordinal = 0)
    )
    private void onRenderStatusEffectOverlay(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci, @Local MobEffectInstance statusEffectInstance, @Local(ordinal = 2) int k, @Local(ordinal = 3) int l) {
        StatusEffectBarRenderer.render(context, statusEffectInstance, k, l, 24, 24, this.config);
        RenderSystem.enableBlend(); // disabled by DrawableHelper#fill
    }
}
