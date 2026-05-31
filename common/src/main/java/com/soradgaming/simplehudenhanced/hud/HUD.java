package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.cache.EquipmentCache;
import com.soradgaming.simplehudenhanced.cache.MovementCache;
import com.soradgaming.simplehudenhanced.cache.StatusCache;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.config.TextAlignment;
import com.soradgaming.simplehudenhanced.utli.Colours;
import com.soradgaming.simplehudenhanced.utli.Utilities;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class HUD {
    private static HUD instance;  // Singleton instance

    // Minecraft client variables
    private final Minecraft client;
    private final Font renderer;

    //Config
    private final SimpleHudEnhancedConfig config;

    // Cache
    private EquipmentCache equipmentCache;
    private MovementCache movementCache;
    private StatusCache statusCache;

    // Sprint Timer Variables
    public boolean sprintTimerRunning = false;  // Variable to store if the timer is running
    public long sprintTimer = 3000;  // X seconds in milliseconds (default 3 seconds)

    private HUD(Minecraft client, SimpleHudEnhancedConfig config) {
        this.client = client;
        this.renderer = client.font;
        this.config = config;
    }

    // Initialization method (called once)
    public static void initialize(Minecraft client, SimpleHudEnhancedConfig config) {
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

    public void drawHud(GuiGraphics context) {
        // Check if HUD is enabled
        if (!config.uiConfig.toggleSimpleHUDEnhanced) return;

        // Instance of Class with all the Game Information
        GameInfo GameInformation = new GameInfo(this.client, config);

        // Draw HUD
        drawStatusElements(context, GameInformation);

        // Draw Equipment Status
        if (config.toggleEquipmentStatus) {
            Equipment equipment = new Equipment(context, config, equipmentCache);
            equipment.init();
        }

        // Draw Movement Status
        if (config.toggleMovementStatus) {
            Movement movement = new Movement(context, config, movementCache);
            if (config.movementStatus.toggleMovementStatus) {
                movement.init(GameInformation);
            }
            if (sprintTimerRunning || !config.paperDoll.togglePaperDollTimer) {
                movement.drawPaperDoll(context);
            }
        }

        // Draw Time
        drawTime(context, statusCache.getSystemTime());

        // Ensure the Hud is drawn (thx rk_01)
        context.flush();
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

    private void drawStatusElements(GuiGraphics context, GameInfo gameInformation) {
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
                BoxWidth = this.renderer.width(s);
            }
        }

        int lineHeight = (this.renderer.lineHeight); // TODO - Make this configurable

        // Screen Manager
        ScreenManager screenManager = new ScreenManager(this.client.getWindow().getGuiScaledWidth(), this.client.getWindow().getGuiScaledHeight());
        screenManager.setPadding(4);
        int xAxis = screenManager.calculateXAxis(Xcords, Scale, BoxWidth);
        int yAxis = screenManager.calculateYAxis(lineHeight, hudInfo.size(), Ycords, Scale);
        screenManager.setScale(context, Scale);

        for (String line : hudInfo) {
            int offset = 0;
            if (config.uiConfig.textAlignment == TextAlignment.Right) {
                int lineLength = this.renderer.width(line);
                offset = (BoxWidth - lineLength);
            } else if (config.uiConfig.textAlignment == TextAlignment.Center) {
                int lineLength = this.renderer.width(line);
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
                context.fill(xAxis + offset - 1, yAxis - 1, xAxis + offset + this.renderer.width(line), yAxis + lineHeight - 1, 0x80000000);
            }
            context.drawString(this.renderer, line, xAxis + offset, yAxis, colour);
            yAxis += lineHeight;
        }

        screenManager.resetScale(context);
    }

    private void drawTime(GuiGraphics context, String systemTime) {
        // Screen Manager
        ScreenManager timeScreenManager = new ScreenManager(this.client.getWindow().getGuiScaledWidth(), this.client.getWindow().getGuiScaledHeight());
        timeScreenManager.setPadding(2);
        float timeScale = (float) config.statusElements.systemTime.textScale / 100;
        int xAxisTime = timeScreenManager.calculateXAxis(100, timeScale, this.renderer.width(systemTime));
        int yAxisTime = timeScreenManager.calculateYAxis(this.renderer.lineHeight, 1, 100, timeScale);
        timeScreenManager.setScale(context, timeScale);

        if (config.statusElements.systemTime.textBackground) {
            // Draw Background
            context.fill(xAxisTime - 1, yAxisTime - 1, xAxisTime + this.renderer.width(systemTime), yAxisTime + this.renderer.lineHeight - 1, 0x80000000);
        }

        // Draw System Time on Bottom Right of Screen
        context.drawString(this.renderer, systemTime, xAxisTime, yAxisTime, config.uiConfig.textColor);

        timeScreenManager.resetScale(context);
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
