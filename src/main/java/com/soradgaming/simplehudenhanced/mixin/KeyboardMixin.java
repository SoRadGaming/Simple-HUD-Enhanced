package com.soradgaming.simplehudenhanced.mixin;

import com.soradgaming.simplehudenhanced.debugStatus.DebugStatus;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;client:Lnet/minecraft/client/MinecraftClient;", ordinal = 1))
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (key == GLFW.GLFW_KEY_UNKNOWN) {
            DebugStatus.setDebugStatus(true);
        }
    }

    @Final
    @Shadow private net.minecraft.client.MinecraftClient client;
    @Shadow private boolean switchF3State;

    @Inject(method = "onKey", at = @At("TAIL"))
    private void injectCustomCodeAfterDebugToggle(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (!this.switchF3State && key == 292 && window == this.client.getWindow().getHandle() && action == 0) {
            DebugStatus.setDebugStatus(!DebugStatus.getDebugStatus());
        }
    }
}
