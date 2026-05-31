package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.utli.TpsTracker;
import com.soradgaming.simplehudenhanced.utli.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class GameInfo {
    private final Minecraft client;
    private LocalPlayer player;
    private final SimpleHudEnhancedConfig config;

    public GameInfo(Minecraft client, SimpleHudEnhancedConfig config) {
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
        return String.format("%d, %d, %d", this.player.blockPosition().getX(), this.player.blockPosition().getY(), this.player.blockPosition().getZ());
    }

    public String getBiome() {
        if (!config.statusElements.Biome.toggleBiome) {
            return "";
        }

        if (this.client.level == null) {return "";}

        return Utilities.getBiome(this.client.level, this.player, config.statusElements.Biome.toggleBiomeLabel);
    }

    public String getDirection() {
        if (!config.statusElements.coordinates.toggleCoordinates || !config.statusElements.coordinates.toggleDirection) {
            return "";
        }
        if (config.statusElements.coordinates.toggleOffset) {
            return String.format(" (%s", Utilities.translatable("text.direction.simplehudenhanced." + this.player.getDirection().getSerializedName()).getString());
        } else {
            return String.format(" (%s)", Utilities.translatable("text.direction.simplehudenhanced." + this.player.getDirection().getSerializedName()).getString());
        }
    }

    public String getNether() {
        if (!config.statusElements.coordinates.toggleCoordinates || !config.statusElements.coordinates.toggleNetherCoordinateConversion) {
            return "";
        }
        String coordsFormat = "X: %.0f, Z: %.0f";
        if (this.player.level().dimension().location().toString().equals("minecraft:overworld")) {
            return (Utilities.translatable("text.hud.simplehudenhanced.nether").getString() + ": " + String.format(coordsFormat, this.player.getX() / 8, this.player.getZ() / 8));
        } else if (this.player.level().dimension().location().toString().equals("minecraft:the_nether")) {
            return(Utilities.translatable("text.hud.simplehudenhanced.overworld").getString() + ": " + String.format(coordsFormat, this.player.getX() * 8, this.player.getZ() * 8));
        }
        return "";
    }

    public String getChunkCords() {
        if (!config.statusElements.coordinates.toggleCoordinates || !config.statusElements.coordinates.toggleChunkCoordinates) {
            return "";
        }
        return(Utilities.translatable("text.hud.simplehudenhanced.chunk").getString() + ": " + String.format("%d, %d, %d", this.player.blockPosition().getX() >> 4, this.player.blockPosition().getY() >> 4, this.player.blockPosition().getZ() >> 4));
    }

    public String getSubChunkCords() {
        if (!config.statusElements.coordinates.toggleCoordinates || !config.statusElements.coordinates.toggleSubChunkCoordinates) {
            return "";
        }
        return(Utilities.translatable("text.hud.simplehudenhanced.subchunk").getString() + ": " + String.format("%d, %d, %d", this.player.blockPosition().getX() & 0xF, this.player.blockPosition().getY() & 0xF, this.player.blockPosition().getZ() & 0xF));
    }

    public String getOffset() {
        if (!config.statusElements.coordinates.toggleCoordinates || !config.statusElements.coordinates.toggleOffset) {
            return "";
        }
        Direction facing = this.player.getDirection();
        String offset = "";

        if (facing.getStepX() > 0) {
            offset += "+X";
        } else if (facing.getStepX() < 0) {
            offset += "-X";
        }

        if (facing.getStepZ() > 0) {
            offset += "+Z";
        } else if (facing.getStepZ() < 0) {
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

    // Chunk
    public String getChunkCount() {
        if (!config.statusElements.counters.chunkCount.toggleChunkCount) {
            return "";
        }

        if (this.client.level == null) {
            return "";
        }

        if (config.statusElements.counters.chunkCount.toggleTotal && config.statusElements.counters.chunkCount.toggleLoaded) {
            return String.format("C: %s", this.client.level.getChunkSource().gatherStats());
        } else {
            if (config.statusElements.counters.chunkCount.toggleLoaded) {
                return String.format("C: %s", this.client.level.getChunkSource().getLoadedChunksCount());
            } else if (config.statusElements.counters.chunkCount.toggleTotal) {
                return String.format("C: %s", this.client.level.getChunkSource().gatherStats().split(",")[0]); // TODO test
            }
        }

        return "";
    }

    // Entity
    public String getEntityCount() {
        if (!config.statusElements.counters.toggleEntityCount) {
            return "";
        }
        if (this.client.level != null) {
            return String.format("E: %d", this.client.level.getEntityCount());
        }
        return "";
    }

    // Particles
    public String getParticleCount() {
        if (!config.statusElements.counters.toggleParticleCount) {
            return "";
        }
        return String.format("P: %s", this.client.particleEngine.countParticles());
    }

    public String getSpeed() {
        if (!config.statusElements.playerSpeed.togglePlayerSpeed) {
            return "";
        }

        Vec3 playerPosVec = this.player.position();
        double travelledX = playerPosVec.x - this.player.xo;
        double travelledZ = playerPosVec.z - this.player.zo;
        double currentSpeed = Mth.sqrt((float)(travelledX * travelledX + travelledZ * travelledZ));

        if (config.statusElements.playerSpeed.togglePlayerVerticalSpeed) {
            double currentVertSpeed = playerPosVec.y - this.player.yo;
            currentSpeed = Mth.sqrt((float)(currentSpeed * currentSpeed + currentVertSpeed * currentVertSpeed));
        }

        return String.format("%.2f m/s", currentSpeed / 0.05F);
    }

    public String getLightLevel() {
        if (!config.statusElements.toggleLightLevel) {
            return "";
        }
        return String.format(Utilities.translatable("text.hud.simplehudenhanced.lightlevel").getString() + ": %d", this.player.level().getMaxLocalRawBrightness(this.player.blockPosition()));
    }

    public String getTime() {
        if (!config.statusElements.gameTime.toggleGameTime) {
            return "";
        }

        long time = this.player.level().getDayTime();

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

    public String getDay() {
        if (!config.statusElements.gameTime.toggleGameDayCounter) {
            return "";
        }
        long time = this.player.level().getDayTime();
        long day = (time / 24000);
        return String.format(Utilities.translatable("text.hud.simplehudenhanced.day").getString() + ": %d", day);
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
            return String.format("%s " + Utilities.translatable("text.hud.simplehudenhanced.ping").getString(), this.client.getConnection().getPlayerInfo(this.player.getUUID()).getLatency());
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
            return String.format(Utilities.translatable("text.hud.simplehudenhanced.server").getString() + ": %s", this.client.getCurrentServer().name);
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getServerAddress() {
        if (!config.statusElements.toggleServerAddress) {
            return "";
        }
        try {
            return String.format(Utilities.translatable("text.hud.simplehudenhanced.serveraddress").getString() + ": %s", this.client.getCurrentServer().ip);
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
        return this.player.isShiftKeyDown();
    }
}
