package com.soradgaming.simplehudenhanced.mixin;

import com.soradgaming.simplehudenhanced.hud.HUD;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(MinecraftClient.class)
public class MovementMixin {
    @Unique
    private long sprintTimerStart = 0L;  // Variable to store the timer start time

    @Inject(method = "handleInputEvents", at = @At("TAIL"))
    private void onHandleInputEvents(CallbackInfo info) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null && HUD.getInstance() != null) {
            if (player.isSneaking() || player.isGliding() || player.isSprinting() || player.isSwimming()) {
                // Start or extend the timer when any valid input is given
                sprintTimerStart = System.currentTimeMillis();
                HUD.getInstance().sprintTimerRunning = true;
            }

            // Check if X seconds have passed since the timer started
            if (System.currentTimeMillis() - sprintTimerStart >= HUD.getInstance().sprintTimer) {
                HUD.getInstance().sprintTimerRunning = false;
            }
        }
    }
}
