package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;

public class HUD {
    // Minecraft client variables
    private final MinecraftClient client;
    private final TextRenderer renderer;

    //Config
    private SimpleHudEnhancedConfig config;

    public HUD(MinecraftClient client) {
        this.client = client;

        this.renderer = client.textRenderer;

        this.config = AutoConfig.getConfigHolder(SimpleHudEnhancedConfig.class).getConfig();

        AutoConfig.getConfigHolder(SimpleHudEnhancedConfig.class).registerSaveListener((manager, data) -> {
            // Update local config when new settings are saved
            this.config = data;
            return ActionResult.SUCCESS;
        });
    }

    // Main HUD function to draw all the elements on the screen
    public void drawHud(MatrixStack matrixStack) {
        // Check if HUD is enabled
        if (!config.uiConfig.toggleSimpleHUDEnhanced) return;

        // Get all the lines to be displayed
        GameInfo GameInformation = new GameInfo(this.client);
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

        //Remove empty lines from the array
        hudInfo.removeIf(String::isEmpty);

        // Draw HUD
        int Xcords = config.statusElements.Xcords;
        int Ycords = config.statusElements.Ycords;

        // Get the longest string in the array
        int longestString = 0;
        int BoxWidth = 0;
        for (String s : hudInfo) {
            if (s.length() > longestString) {
                longestString = s.length();
                BoxWidth = this.renderer.getWidth(s);
            }
        }

        int lineHeight = this.renderer.fontHeight + 2;
        int yAxis = (((this.client.getWindow().getScaledHeight()) - ((lineHeight + 4) * hudInfo.size())) + (lineHeight + 4)) * (Ycords) / 100;
        int xAxis = ((this.client.getWindow().getScaledWidth() - 4) - (BoxWidth)) * Xcords / 100;

        // Add Padding to left of the screen
        if (xAxis <= 4) {
            xAxis = 4;
        }

        for (String line : hudInfo) {
            int offset = 0;
            if (Xcords >= 50) {
                int lineLength = this.renderer.getWidth(line);
                offset = (BoxWidth - lineLength);
            }

            this.renderer.drawWithShadow(matrixStack, line, xAxis + offset, yAxis + 4, config.uiConfig.textColor);
            yAxis += lineHeight;
        }

        // Draw Movement Status
        if (config.toggleMovementStatus) {
            Movement movement = new Movement(matrixStack, config);
            movement.init(GameInformation);
        }

        // Draw Equipment Status
        if (config.toggleEquipmentStatus) {
            Equipment equipment = new Equipment(matrixStack, config);
            equipment.init();
        }
    }
}
