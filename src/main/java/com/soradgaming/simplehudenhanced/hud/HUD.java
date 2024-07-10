package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.cache.EquipmentCache;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.config.TextAlignment;
import com.soradgaming.simplehudenhanced.utli.Colours;
import com.soradgaming.simplehudenhanced.utli.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class HUD {
    private static HUD instance;  // Singleton instance

    // Minecraft client variables
    private final MinecraftClient client;
    private final TextRenderer renderer;

    //Config
    private final SimpleHudEnhancedConfig config;

    // Cache
    private EquipmentCache equipmentCache;

    // Sprint Timer Variables
    public boolean sprintTimerRunning = false;  // Variable to store if the timer is running

    private HUD(MinecraftClient client, SimpleHudEnhancedConfig config) {
        this.client = client;
        this.renderer = client.textRenderer;
        this.config = config;
    }

    // Initialization method (called once)
    public static void initialize(MinecraftClient client, SimpleHudEnhancedConfig config) {
        if (instance == null) {
            Logger.getLogger(Utilities.getModName()).warning("New HUD instance created.");
        } else {
            Logger.getLogger(Utilities.getModName()).warning("HUD has already been initialized.");
            Logger.getLogger(Utilities.getModName()).warning("New HUD instance created. (Override)");
        }
        instance = new HUD(client, config);
        // Create Cache
        instance.equipmentCache = EquipmentCache.getInstance(config);
    }

    // Singleton instance getter
    public static HUD getInstance() {
        if (instance == null) {
            Logger.getLogger(Utilities.getModName()).warning("HUD has not been initialized yet.");
            return null;
        }
        return instance;
    }

    public void drawAsyncHud(DrawContext context) {
        // Check if HUD is enabled
        if (!config.uiConfig.toggleSimpleHUDEnhanced) return;

        // Instance of Class with all the Game Information
        GameInfo GameInformation = new GameInfo(this.client, config);

        // Draw HUD
        CompletableFuture<Void> statusElementsFuture = CompletableFuture.runAsync(() -> drawStatusElements(context, GameInformation), MinecraftClient.getInstance()::executeTask);

        // Draw Equipment Status
        CompletableFuture<Void> equipmentFuture = CompletableFuture.runAsync(() -> {
            if (config.toggleEquipmentStatus) {
                Equipment equipment = new Equipment(context, config, equipmentCache);
                equipment.init();
            }
        }, MinecraftClient.getInstance()::executeTask);

        // Draw Movement Status
        if (config.toggleMovementStatus) {
            Movement movement = new Movement(context, config);
            if (config.movementStatus.toggleMovementStatus) {
                movement.init(GameInformation);
            }
            if (sprintTimerRunning) {
                movement.drawPaperDoll(context);
            }
        }

        // Draw Time
        drawTime(context, GameInformation.getSystemTime());

        // Ensure completion of all tasks before moving forward
        CompletableFuture.allOf(equipmentFuture, statusElementsFuture).join();
    }

    @NotNull
    private static ArrayList<String> getHudInfo(GameInfo GameInformation) {
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
        return hudInfo;
    }

    public int getColor(String line, GameInfo GameInformation) {
        int colour = config.uiConfig.textColor;

        // FPS Colour Check
        if (Objects.equals(line, GameInformation.getFPS())) {
            if (config.statusElements.fps.toggleColourFPS) {
                // convert line to int format (102 fps)
                String[] fps = line.split(" ");
                int fpsInt = Integer.parseInt(fps[0]);

                // Check FPS and return colour
                if (fpsInt < 15) {
                    return Colours.RED;
                } else if (fpsInt < 30) {
                    return Colours.lightRed;
                } else if (fpsInt < 45) {
                    return Colours.lightOrange;
                } else if (fpsInt < 60) {
                    return Colours.lightYellow;
                } else {
                    return Colours.GREEN;
                }
            }
        }

        return colour;
    }

    private void drawStatusElements(DrawContext context, GameInfo gameInformation) {
        // Get all the lines to be displayed
        ArrayList<String> hudInfo = getHudInfo(gameInformation);

        //Remove empty lines from the array
        hudInfo.removeIf(String::isEmpty);

        // Draw HUD
        int Xcords = config.statusElements.Xcords;
        int Ycords = config.statusElements.Ycords;
        float Scale = (float) config.uiConfig.textScale / 100;

        // Get the longest string in the array
        int longestString = 0;
        int BoxWidth = 0;
        for (String s : hudInfo) {
            if (s.length() > longestString) {
                longestString = s.length();
                BoxWidth = this.renderer.getWidth(s);
            }
        }

        int lineHeight = (this.renderer.fontHeight);

        // Screen Manager
        ScreenManager screenManager = new ScreenManager(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
        screenManager.setPadding(4);
        int xAxis = screenManager.calculateXAxis(Xcords, Scale, BoxWidth);
        int yAxis = screenManager.calculateYAxis(lineHeight, hudInfo.size(), Ycords, Scale);
        screenManager.setScale(context, Scale);

        for (String line : hudInfo) {
            int offset = 0;
            if (config.uiConfig.textAlignment == TextAlignment.Right) {
                int lineLength = this.renderer.getWidth(line);
                offset = (BoxWidth - lineLength);
            } else if (config.uiConfig.textAlignment == TextAlignment.Center) {
                int lineLength = this.renderer.getWidth(line);
                offset = (BoxWidth - lineLength) / 2;
            }
            // Colour Check
            int colour = getColor(line, gameInformation);
            // Render the line
            if (config.uiConfig.textBackground) {
                // Draw Background
                context.fill(xAxis + offset - 1, yAxis - 1, xAxis + offset + this.renderer.getWidth(line), yAxis + lineHeight - 1, 0x80000000);
            }
            context.drawTextWithShadow(this.renderer, line, xAxis + offset, yAxis, colour);
            yAxis += lineHeight;
        }

        screenManager.resetScale(context);
    }

    private void drawTime(DrawContext context, String systemTime) {
        // Screen Manager
        ScreenManager timeScreenManager = new ScreenManager(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
        timeScreenManager.setPadding(2);
        float timeScale = (float) config.statusElements.systemTime.textScale / 100;
        int xAxisTime = timeScreenManager.calculateXAxis(100, timeScale, this.renderer.getWidth(systemTime));
        int yAxisTime = timeScreenManager.calculateYAxis(this.renderer.fontHeight, 1, 100, timeScale);
        timeScreenManager.setScale(context, timeScale);

        if (config.statusElements.systemTime.textBackground) {
            // Draw Background
            context.fill(xAxisTime - 1, yAxisTime - 1, xAxisTime + this.renderer.getWidth(systemTime), yAxisTime + this.renderer.fontHeight - 1, 0x80000000);
        }

        // Draw System Time on Bottom Right of Screen
        context.drawTextWithShadow(this.renderer, systemTime, xAxisTime, yAxisTime, config.uiConfig.textColor);

        timeScreenManager.resetScale(context);
    }

    public EquipmentCache getEquipmentCache() {
        return equipmentCache;
    }
}
