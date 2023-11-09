package com.soradgaming.simplehudenhanced.mixin;

import com.soradgaming.simplehudenhanced.debugStatus.DebugStatus;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Final
    @Shadow private net.minecraft.client.MinecraftClient client;
    @Shadow private boolean switchF3State;
    @Shadow private long debugCrashStartTime;

    @Inject(method = "onKey", at = @At("HEAD"))
    private void injectCustomCodeAfterDebugToggle(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (window == this.client.getWindow().getHandle()) {
            boolean bl = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_F3);

            if (this.client.currentScreen != null) {
                return;
            }

            if (action == 0) {
                if (key == GLFW.GLFW_KEY_F3 && !this.switchF3State) {
                    DebugStatus.setDebugStatus(!DebugStatus.getDebugStatus());
                }
            } else {
                if (bl) {
                    this.processF3(key);
                }
            }
        }
    }

    @Unique
    private void processF3(int key) {
        if (this.debugCrashStartTime > 0L && this.debugCrashStartTime < Util.getMeasuringTimeMs() - 100L) {
            return;
        }
        if (getVersion().equals("1.20.2")) {
            switch (key) {
                case GLFW.GLFW_KEY_1, GLFW.GLFW_KEY_2, GLFW.GLFW_KEY_3:
                    DebugStatus.setDebugStatus(true);
            }
        }
    }

    @Unique
    private String getVersion() {
        String input = this.client.getGameVersion();
        String output = "";
        // Find the last index of "-"
        int lastIndex = input.lastIndexOf("-");
        // Check if "-" is found
        if (lastIndex != -1) {
            // Extract the substring starting from the character after "-"
            output = input.substring(lastIndex + 1);
        }
        return output;
    }
}



