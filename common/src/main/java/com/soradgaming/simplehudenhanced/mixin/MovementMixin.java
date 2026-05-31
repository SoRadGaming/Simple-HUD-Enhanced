package com.soradgaming.simplehudenhanced.mixin;

import com.soradgaming.simplehudenhanced.hud.HUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MovementMixin {
    @Unique
    private long sprintTimerStart = 0L;  // Variable to store the timer start time

    @Inject(method = "handleKeybinds", at = @At("TAIL"))
    private void onHandleInputEvents(CallbackInfo info) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && HUD.getInstance() != null) {
            if (player.isShiftKeyDown() || player.isFallFlying() || player.isSprinting() || player.isSwimming()) {
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
