package com.soradgaming.simplehudenhanced.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.debugStatus.DebugStatus;
import com.soradgaming.simplehudenhanced.hud.HUD;
import com.soradgaming.simplehudenhanced.hud.StatusEffectBarRenderer;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
@Mixin(value = InGameHud.class)
public class GameRender {
    @Unique
    private HUD hud;
    @Unique
    private SimpleHudEnhancedConfig config;
    @Shadow
    @Final
    private MinecraftClient client;
    @Inject(method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/render/item/ItemRenderer;)V", at = @At(value = "RETURN"))
    private void onInit(MinecraftClient client, ItemRenderer render, CallbackInfo ci) {
        // Get Config
        this.config = AutoConfig.getConfigHolder(SimpleHudEnhancedConfig.class).getConfig();
        // Register Save Listener
        AutoConfig.getConfigHolder(SimpleHudEnhancedConfig.class).registerSaveListener((manager, data) -> {
            // Update local config when new settings are saved
            this.config = data;
            return ActionResult.SUCCESS;
        });
        // Start Mixin
        this.hud = new HUD(client, config);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onDraw(DrawContext context, float esp, CallbackInfo ci) {
        if (!(DebugStatus.getDebugStatus() && !this.client.options.hudHidden)) {
            // Call async rendering
            CompletableFuture.runAsync(() -> this.hud.drawAsyncHud(context), MinecraftClient.getInstance()::executeTask);
        }
    }

    // Injects into the renderStatusEffectOverlay method in the InGameHud class to render the status effect bars on the HUD
    @Inject(method = "renderStatusEffectOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/StatusEffectSpriteManager;getSprite(Lnet/minecraft/entity/effect/StatusEffect;)Lnet/minecraft/client/texture/Sprite;", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onRenderStatusEffectOverlay(
            DrawContext context, CallbackInfo ci,
            Collection<StatusEffectInstance> effects, int beneficialColumn,
            int othersColumn, StatusEffectSpriteManager spriteManager,
            List<Runnable> spriteRunnable, Iterator<StatusEffectInstance> it,
            StatusEffectInstance effect, StatusEffect type, int x, int y) {
        StatusEffectBarRenderer.render(context, effect, x, y, 24, 24, this.config);
        RenderSystem.enableBlend(); // disabled by DrawableHelper#fill
    }

    // Debug Enabled
    @Inject(method = "clear", at = @At("HEAD"))
    private void onClear(CallbackInfo ci) {
        DebugStatus.setDebugStatus(false);
    }
}