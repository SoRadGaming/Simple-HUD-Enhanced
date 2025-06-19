package com.soradgaming.simplehudenhanced.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.hud.HUD;
import com.soradgaming.simplehudenhanced.hud.StatusEffectBarRenderer;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.ActionResult;
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

@Environment(EnvType.CLIENT)
@Mixin(value = InGameHud.class)
public class GameRender {
    @Unique
    private SimpleHudEnhancedConfig config;
    @Shadow
    @Final
    private MinecraftClient client;
    @Inject(method = "<init>(Lnet/minecraft/client/MinecraftClient;)V", at = @At(value = "RETURN"))
    private void onInit(MinecraftClient client, CallbackInfo ci) {
        // Get Config
        this.config = AutoConfig.getConfigHolder(SimpleHudEnhancedConfig.class).getConfig();
        // Register Save Listener
        AutoConfig.getConfigHolder(SimpleHudEnhancedConfig.class).registerSaveListener((manager, data) -> {
            // Update local config when new settings are saved
            this.config = data;

            HUD hud = HUD.getInstance();

            // Update Sprint Timer
            if (hud != null) hud.sprintTimer = data.paperDoll.paperDollTimeOut;

            return ActionResult.SUCCESS;
        });
        // Start Mixin
        HUD.initialize(client, config);

        // Start a new thread to update the equipment cache in the background
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            // Update Equipment Cache
            HUD hud = HUD.getInstance();
            if (hud != null && MinecraftClient.getInstance().player != null) {
                hud.getEquipmentCache().updateCache(MinecraftClient.getInstance().player);
                hud.getMovementCache().updateCache(MinecraftClient.getInstance().player);
                hud.getStatusCache().updateCache();
            }
        }, 0, 50, TimeUnit.MILLISECONDS); // 20 times a second TimeUnit.MILLISECONDS
    }

    // Injects into the renderStatusEffectOverlay method in the InGameHud class to render the status effect bars on the HUD
    @Inject(method = "renderStatusEffectOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIIII)V", ordinal = 0)
    )
    private void onRenderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci, @Local StatusEffectInstance statusEffectInstance, @Local(ordinal = 2) int k, @Local(ordinal = 3) int l) {
        StatusEffectBarRenderer.render(context, statusEffectInstance, k, l, 24, 24, this.config);
    }
}