package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.cache.EquipmentCache;
import com.soradgaming.simplehudenhanced.cache.MovementCache;
import com.soradgaming.simplehudenhanced.cache.StatusCache;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.config.TextAlignment;
import com.soradgaming.simplehudenhanced.utli.Colours;
import com.soradgaming.simplehudenhanced.utli.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
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
    private MovementCache movementCache;
    private StatusCache statusCache;

    // Sprint Timer Variables
    public boolean sprintTimerRunning = false;  // Variable to store if the timer is running
    public long sprintTimer = 3000;  // X seconds in milliseconds (default 3 seconds)

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
        instance.movementCache = MovementCache.getInstance();
        instance.statusCache = StatusCache.getInstance(config);
        // Set Sprint Timer
        instance.sprintTimer = config.paperDoll.paperDollTimeOut;
    }

    // Singleton instance getter
    public static HUD getInstance() {
        if (instance == null) {
            Logger.getLogger(Utilities.getModName()).warning("HUD has not been initialized yet.");
            return null;
        }
        return instance;
    }

    public void drawHud(MatrixStack matrixStack) {
        // Check if HUD is enabled
        if (!config.uiConfig.toggleSimpleHUDEnhanced) return;

        // Instance of Class with all the Game Information
        GameInfo GameInformation = new GameInfo(this.client, config);

        // Draw HUD
        drawStatusElements(matrixStack, GameInformation);

        // Draw Equipment Status
        if (config.toggleEquipmentStatus) {
            Equipment equipment = new Equipment(matrixStack, config, equipmentCache);
            equipment.init();
        }

        // Draw Movement Status
        if (config.toggleMovementStatus) {
            Movement movement = new Movement(matrixStack, config, movementCache);
            if (config.movementStatus.toggleMovementStatus) {
                movement.init(GameInformation);
            }
            if (sprintTimerRunning || !config.paperDoll.togglePaperDollTimer) {
                movement.drawPaperDoll(matrixStack);
            }
        }

        // Draw Time
        drawTime(matrixStack, statusCache.getSystemTime());
    }

    public int getColor(String line, GameInfo GameInformation) {
        int colour = config.uiConfig.textColor;

        // FPS Colour Check
        if (Objects.equals(line, GameInformation.getFPS())) {
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

        return colour;
    }

    private void drawStatusElements(MatrixStack matrixStack, GameInfo gameInformation) {
        // Get all the lines to be displayed
        ArrayList<String> hudInfo = getStatusCache().getHudInfo();

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

        int lineHeight = (this.renderer.fontHeight); // TODO - Make this configurable

        // Screen Manager
        ScreenManager screenManager = new ScreenManager(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
        screenManager.setPadding(4);
        int xAxis = screenManager.calculateXAxis(Xcords, Scale, BoxWidth);
        int yAxis = screenManager.calculateYAxis(lineHeight, hudInfo.size(), Ycords, Scale);
        screenManager.setScale(matrixStack, Scale);

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
            int colour = config.uiConfig.textColor;
            if (config.statusElements.fps.toggleColourFPS) {
                colour = getColor(line, gameInformation);
            }
            // Render the line
            if (config.uiConfig.textBackground) {
                // Draw Background
                DrawableHelper.fill(matrixStack, xAxis + offset - 1, yAxis - 1, xAxis + offset + this.renderer.getWidth(line), yAxis + lineHeight - 1, 0x80000000);
            }
            this.renderer.drawWithShadow(matrixStack, line, xAxis + offset, yAxis, colour);
            yAxis += lineHeight;
        }

        screenManager.resetScale(matrixStack);
    }

    private void drawTime(MatrixStack matrixStack, String systemTime) {
        // Screen Manager
        ScreenManager timeScreenManager = new ScreenManager(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
        timeScreenManager.setPadding(2);
        float timeScale = (float) config.statusElements.systemTime.textScale / 100;
        int xAxisTime = timeScreenManager.calculateXAxis(100, timeScale, this.renderer.getWidth(systemTime));
        int yAxisTime = timeScreenManager.calculateYAxis(this.renderer.fontHeight, 1, 100, timeScale);
        timeScreenManager.setScale(matrixStack, timeScale);

        if (config.statusElements.systemTime.textBackground) {
            // Draw Background
            DrawableHelper.fill(matrixStack, xAxisTime - 1, yAxisTime - 1, xAxisTime + this.renderer.getWidth(systemTime), yAxisTime + this.renderer.fontHeight - 1, 0x80000000);
        }

        // Draw System Time on Bottom Right of Screen
        this.renderer.drawWithShadow(matrixStack, systemTime, xAxisTime, yAxisTime, config.uiConfig.textColor);

        timeScreenManager.resetScale(matrixStack);
    }

    public EquipmentCache getEquipmentCache() {
        return equipmentCache;
    }

    public MovementCache getMovementCache() {
        return movementCache;
    }

    public StatusCache getStatusCache() {
        return statusCache;
    }
}
