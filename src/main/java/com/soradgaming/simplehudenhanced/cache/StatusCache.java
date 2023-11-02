package com.soradgaming.simplehudenhanced.cache;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.hud.EquipmentInfoStack;
import com.soradgaming.simplehudenhanced.hud.GameInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StatusCache {
    private static StatusCache instance;
    private ArrayList<String> hudInfo;

    private final GameInfo gameInformation;

    public int cacheUpdates = 0;

    private StatusCache(MinecraftClient client) {
        // Instance of Class with all the Game Information
        gameInformation = new GameInfo(client);
        // Register Events
        registerEvent();
    }

    public static StatusCache getInstance(MinecraftClient client) {
        if(instance == null) {
            instance = new StatusCache(client);
        }
        return instance;
    }

    private void registerEvent() {
        UpdateCacheEvent.EVENT.register((cache) -> {
            if (cache == Cache.STATUS) {
                // Run Code on Event
                cacheUpdates++;
                setCacheValid(false);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }

    private void updateCache() {
        createStatusList();
        setCacheValid(true);
    }

    private boolean isCacheValid = false;

    private boolean isCacheValid() {
        return isCacheValid;
    }

    public void setCacheValid(boolean cacheValid) {
        isCacheValid = cacheValid;
    }

    private void createStatusList() {
        // Get all the lines to be displayed
         hudInfo = getHudInfo(gameInformation);
        //Remove empty lines from the array
        hudInfo.removeIf(String::isEmpty);
    }

    public List<String> getStatusElements() {
        if (!isCacheValid()) {
            updateCache();
        }
        return hudInfo;
    }

    @NotNull
    private static ArrayList<String> getHudInfo(GameInfo GameInformation) {
        ArrayList<String> hudInfo = new ArrayList<>();

        // Add all the lines to the array
        hudInfo.add(GameInformation.getCords() + GameInformation.getDirection() + GameInformation.getOffset());
        hudInfo.add(GameInformation.getNether());
        hudInfo.add(GameInformation.getFPS());
        hudInfo.add(GameInformation.getSpeed());
        hudInfo.add(GameInformation.getLightLevel());
        hudInfo.add(GameInformation.getBiome());
        hudInfo.add(GameInformation.getTime());
        hudInfo.add(GameInformation.getPlayerName());
        hudInfo.add(GameInformation.getPing());
        hudInfo.add(GameInformation.getServer());
        hudInfo.add(GameInformation.getServerAddress());
        return hudInfo;
    }
}
