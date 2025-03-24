package com.soradgaming.simplehudenhanced.cache;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.hud.GameInfo;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;

public class StatusCache {
    private static StatusCache instance; // Singleton instance
    private final SimpleHudEnhancedConfig config;
    // Deadlock prevention
    private boolean hudInfoDeadlock;
    private ArrayList<String> hudInfo;
    private ArrayList<String> hudInfoOLD;
    private boolean systemTimeDeadlock;
    private String systemTime;
    private String systemTimeOLD;

    private StatusCache(SimpleHudEnhancedConfig config) {
        this.config = config;
    }

    public static StatusCache getInstance(SimpleHudEnhancedConfig config) {
        if(instance == null) {
            instance = new StatusCache(config);
        }
        return instance;
    }

    public void updateCache() {
        GameInfo gameInfo = new GameInfo(MinecraftClient.getInstance(), config);
        calculateHudInfo(gameInfo);
        calculateSystemTime(gameInfo);
    }

    public synchronized ArrayList<String> getHudInfo() {
        if (hudInfoDeadlock) {
            return hudInfoOLD;
        } else {
            return hudInfo;
        }
    }

    private void calculateHudInfo(GameInfo GameInformation) {
        hudInfoOLD = this.hudInfo;
        hudInfoDeadlock = true;
        ArrayList<String> hudInfo = new ArrayList<>();

        // Add all the lines to the array
        hudInfo.add(GameInformation.getCords() + GameInformation.getDirection() + GameInformation.getOffset());
        hudInfo.add(GameInformation.getNether());
        hudInfo.add(GameInformation.getChunkCords());
        hudInfo.add(GameInformation.getSubChunkCords());
        hudInfo.add(GameInformation.getFPS());
        hudInfo.add(GameInformation.getSpeed());
        hudInfo.add(GameInformation.getLightLevel());
        hudInfo.add(GameInformation.getBiome());
        hudInfo.add(GameInformation.getTime());
        hudInfo.add(GameInformation.getDay());
        hudInfo.add(GameInformation.getPlayerName());
        hudInfo.add(GameInformation.getPing());
        hudInfo.add(GameInformation.getTPS());
        hudInfo.add(GameInformation.getServer());
        hudInfo.add(GameInformation.getServerAddress());
        hudInfo.add(GameInformation.getChunkCount());
        hudInfo.add(GameInformation.getEntityCount());
        hudInfo.add(GameInformation.getParticleCount());

        // Remove empty lines from the array
        hudInfo.removeIf(String::isEmpty);
        this.hudInfo = hudInfo;

        hudInfoDeadlock = false;
    }

    public String getSystemTime() {
        if (systemTimeDeadlock) {
            return systemTimeOLD;
        } else {
            return systemTime;
        }
    }

    private void calculateSystemTime(GameInfo GameInformation) {
        this.systemTimeOLD = this.systemTime;
        systemTimeDeadlock = true;
        this.systemTime = GameInformation.getSystemTime();
        systemTimeDeadlock = false;
    }

}
