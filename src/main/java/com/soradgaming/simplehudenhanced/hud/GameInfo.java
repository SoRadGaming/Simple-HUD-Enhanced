package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.utli.TpsTracker;
import com.soradgaming.simplehudenhanced.utli.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class GameInfo {
    private final MinecraftClient client;
    private ClientPlayerEntity player;
    private final SimpleHudEnhancedConfig config;

    public GameInfo(MinecraftClient client, SimpleHudEnhancedConfig config) {
        this.client = client;
        this.config = config;

        if (this.client.player != null) {
            this.player = this.client.player;
        } else {
            Exception e = new Exception("Player is null");
            e.printStackTrace();
        }
    }

    public String getCords() {
        if (!config.statusElements.coordinates.toggleCoordinates) {
            return "";
        }
        return String.format("%d, %d, %d", this.player.getBlockPos().getX(), this.player.getBlockPos().getY(), this.player.getBlockPos().getZ());
    }

    public String getBiome() {
        if (!config.statusElements.toggleBiome) {
            return "";
        }

        if (this.client.world == null) {return "";}

        return Utilities.getBiome(this.client.world, this.player);
    }

    public String getDirection() {
        if (!config.statusElements.coordinates.toggleCoordinates || !config.statusElements.coordinates.toggleDirection) {
            return "";
        }
        if (config.statusElements.coordinates.toggleOffset) {
            return String.format(" (%s", Utilities.capitalise(this.player.getHorizontalFacing().asString()));
        } else {
            return String.format(" (%s)", Utilities.capitalise(this.player.getHorizontalFacing().asString()));
        }
    }

    public String getNether() {
        if (!config.statusElements.coordinates.toggleCoordinates || !config.statusElements.coordinates.toggleNetherCoordinateConversion) {
            return "";
        }
        String coordsFormat = "X: %.0f, Z: %.0f";
        if (this.player.getWorld().getRegistryKey().getValue().toString().equals("minecraft:overworld")) {
            return (Utilities.translatable("text.hud.simplehudenhanced.nether").getString() + ": " + String.format(coordsFormat, this.player.getX() / 8, this.player.getZ() / 8));
        } else if (this.player.getWorld().getRegistryKey().getValue().toString().equals("minecraft:the_nether")) {
            return(Utilities.translatable("text.hud.simplehudenhanced.overworld").getString() + ": " + String.format(coordsFormat, this.player.getX() * 8, this.player.getZ() * 8));
        }
        return "";
    }

    public String getOffset() {
        if (!config.statusElements.coordinates.toggleCoordinates || !config.statusElements.coordinates.toggleOffset) {
            return "";
        }
        Direction facing = this.player.getHorizontalFacing();
        String offset = "";

        if (facing.getOffsetX() > 0) {
            offset += "+X";
        } else if (facing.getOffsetX() < 0) {
            offset += "-X";
        }

        if (facing.getOffsetZ() > 0) {
            offset += "+Z";
        } else if (facing.getOffsetZ() < 0) {
            offset += "-Z";
        }

        if (config.statusElements.coordinates.toggleDirection) {
            offset = " " + offset + ")";
        } else {
            offset = " (" + offset + ")";
        }

        return offset;
    }

    public String getFPS() {
        if (!config.statusElements.fps.toggleFPS) {
            return "";
        }
        return Utilities.getFPS(this.client);
    }

    public String getSpeed() {
        if (!config.statusElements.playerSpeed.togglePlayerSpeed) {
            return "";
        }

        Vec3d playerPosVec = this.player.getPos();
        double travelledX = playerPosVec.x - this.player.prevX;
        double travelledZ = playerPosVec.z - this.player.prevZ;
        double currentSpeed = MathHelper.sqrt((float)(travelledX * travelledX + travelledZ * travelledZ));

        if (config.statusElements.playerSpeed.togglePlayerVerticalSpeed) {
            double currentVertSpeed = playerPosVec.y - this.player.prevY;
            currentSpeed = MathHelper.sqrt((float)(currentSpeed * currentSpeed + currentVertSpeed * currentVertSpeed));
        }

        return String.format("%.2f m/s", currentSpeed / 0.05F);
    }

    public String getLightLevel() {
        if (!config.statusElements.toggleLightLevel) {
            return "";
        }
        return String.format(Utilities.translatable("text.hud.simplehudenhanced.lightlevel").getString() + ": %d", this.player.getWorld().getLightLevel(this.player.getBlockPos()));
    }

    public String getTime() {
        if (!config.statusElements.gameTime.toggleGameTime) {
            return "";
        }

        long time = this.player.getWorld().getTimeOfDay();

        if (config.statusElements.gameTime.toggleGameTime24Hour) {
            //24-hour format
            long hour = (time / 1000 + 6) % 24;
            int minute = (int) ((time % 1000) / 1000.0 * 60);
            return String.format("%d:%02d", hour, minute);
        }

        // 12-hour format
        long hour = (time / 1000 + 6) % 24;
        int minute = (int) ((time % 1000) / 1000.0 * 60);

        String ampm = "AM";
        if (hour >= 12) {
            ampm = "PM";
        }

        if (hour > 12) {
            hour -= 12;
        }
        if (hour == 0) {
            hour = 12;
        }

        return String.format("%d:%02d %s", hour, minute, ampm);
    }

    public String getSystemTime() {
        if (!config.statusElements.systemTime.toggleSystemTime) {
            return "";
        }

        java.time.LocalDateTime time = java.time.LocalDateTime.now();

        // 12-hour format
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("h:mm a");

        if (config.statusElements.systemTime.toggleSystemTime24Hour) {
            // 24-hour format
            formatter = java.time.format.DateTimeFormatter.ofPattern("H:mm");
        }

        return(time.format(formatter).toUpperCase());
    }

    public String getPlayerName() {
        if (!config.statusElements.togglePlayerName) {
            return "";
        }
        return String.format(Utilities.translatable("text.hud.simplehudenhanced.player").getString() + ": %s", this.player.getName().getString());
    }

    public String getPing() {
        if (!config.statusElements.togglePing) {
            return "";
        }
        try {
            return String.format("%s " + Utilities.translatable("text.hud.simplehudenhanced.ping").getString(), this.client.getNetworkHandler().getPlayerListEntry(this.player.getUuid()).getLatency());
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getTPS() {
        if (!config.statusElements.toggleTPS) {
            return "";
        }
        return String.format(Utilities.translatable("text.hud.simplehudenhanced.tps").getString() + ": %.2f", TpsTracker.INSTANCE.getTickRate());
    }

    public String getServer() {
        if (!config.statusElements.toggleServerName) {
            return "";
        }
        try {
            return String.format(Utilities.translatable("text.hud.simplehudenhanced.server").getString() + ": %s", this.client.getCurrentServerEntry().name);
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getServerAddress() {
        if (!config.statusElements.toggleServerAddress) {
            return "";
        }
        try {
            return String.format(Utilities.translatable("text.hud.simplehudenhanced.serveraddress").getString() + ": %s", this.client.getCurrentServerEntry().address);
        } catch (NullPointerException e) {
            return "";
        }
    }

    public boolean isPlayerSprinting() {
        // Done this way to ensure null safety
        return this.player.isSprinting();
    }

    public boolean isPlayerFlying() {
        // Done this way to ensure null safety
        return this.player.isFallFlying();
    }

    public boolean isPlayerSwimming() {
        // Done this way to ensure null safety
        return this.player.isSwimming();
    }

    public boolean isPlayerSneaking() {
        // Done this way to ensure null safety
        return this.player.isSneaking();
    }
}
