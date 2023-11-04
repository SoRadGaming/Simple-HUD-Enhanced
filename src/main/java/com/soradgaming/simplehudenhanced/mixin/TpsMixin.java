package com.soradgaming.simplehudenhanced.mixin;

import com.soradgaming.simplehudenhanced.utli.TpsTracker;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class TpsMixin {
    @Mixin(ClientConnection.class)
    public static class ClientConnectionMixin {
        @Inject(method = "handlePacket", at = @At("HEAD"))
        private static <T extends PacketListener> void onHandlePacket(Packet<T> packet, PacketListener packetListener, CallbackInfo ci) {
            TpsTracker.INSTANCE.onPacketReceive(packet);
        }
    }
    @Mixin(ClientPlayNetworkHandler.class)
    public static class ClientPlayNetworkHandlerMixin {
        @Inject(method = "onGameJoin", at = @At("TAIL"))
        private void triggerJoinEvent(GameJoinS2CPacket packet, CallbackInfo info) {
            TpsTracker.INSTANCE.onGameJoined();
        }
    }
}
