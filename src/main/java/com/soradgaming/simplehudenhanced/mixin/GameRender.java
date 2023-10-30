package com.soradgaming.simplehudenhanced.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.soradgaming.simplehudenhanced.hud.HUD;
import com.soradgaming.simplehudenhanced.hud.StatusEffectBarRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
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
    @Shadow
    @Final
    private MinecraftClient client;
    @Inject(method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/render/item/ItemRenderer;)V", at = @At(value = "RETURN"))
    private void onInit(MinecraftClient client, ItemRenderer render, CallbackInfo ci) {
        // Start Mixin
        this.hud = new HUD(client);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onDraw(MatrixStack matrixStack, float esp, CallbackInfo ci) {
        if (!this.client.options.debugEnabled) {
            // Call async rendering
            CompletableFuture.runAsync(() -> this.hud.drawAsyncHud(matrixStack), MinecraftClient.getInstance()::executeTask);
        }
    }

    // Injects into the renderStatusEffectOverlay method in the InGameHud class to render the status effect bars on the HUD
    @Inject(method = "renderStatusEffectOverlay",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/StatusEffectSpriteManager;getSprite(Lnet/minecraft/entity/effect/StatusEffect;)Lnet/minecraft/client/texture/Sprite;", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onRenderStatusEffectOverlay(
            MatrixStack matrixStack, CallbackInfo ci,
            Collection<StatusEffectInstance> effects, int beneficialColumn,
            int othersColumn, StatusEffectSpriteManager spriteManager,
            List<Runnable> spriteRunnable, Iterator<StatusEffectInstance> it,
            StatusEffectInstance effect, StatusEffect type, int x, int y) {
        StatusEffectBarRenderer.render(matrixStack, effect, x, y, 24, 24);
        RenderSystem.enableBlend(); // disabled by DrawableHelper#fill
    }
}