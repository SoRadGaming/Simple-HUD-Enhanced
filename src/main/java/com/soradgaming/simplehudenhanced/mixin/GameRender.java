package com.soradgaming.simplehudenhanced.mixin;

import com.soradgaming.simplehudenhanced.hud.HUD;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = InGameHud.class)
public class GameRender {
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
            // Draw Game info on every GameHud render
            this.hud.drawHud(matrixStack);
        }
    }
}