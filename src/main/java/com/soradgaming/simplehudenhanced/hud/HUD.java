package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.utli.Utilities;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
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
    public void drawHud(DrawContext context) {
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

            context.drawTextWithShadow(this.renderer, line, xAxis + offset, yAxis + 4, config.uiConfig.textColor);
            yAxis += lineHeight;
        }

        // Draw Movement Status
        if (config.toggleMovementStatus) {
            if (config.movementStatus.toggleSwimmingStatus && GameInformation.isPlayerSwimming()) {
                draw(context, "text.hud.simplehudenhanced.swimming");
            } else if (config.movementStatus.toggleFlyingStatus && GameInformation.isPlayerFlying()) {
                draw(context, "text.hud.simplehudenhanced.flying");
            } else if (config.movementStatus.toggleSneakStatus && GameInformation.isPlayerSneaking()) {
                draw(context, "text.hud.simplehudenhanced.sneaking");
            } else if (config.movementStatus.toggleSprintStatus && GameInformation.isPlayerSprinting()) {
                draw(context, "text.hud.simplehudenhanced.sprinting");
            }
        }
    }

    // Draw the movement status on the screen
    private void draw(DrawContext context, String textKey) {
        final String text = Utilities.translatable(textKey).getString();

        int yAxis = (((this.client.getWindow().getScaledHeight() - this.renderer.fontHeight + 2) - 4) * (config.movementStatus.movementStatusLocationY) / 100);
        int xAxis = (((this.client.getWindow().getScaledWidth() - this.renderer.getWidth(text)) - 4) * (config.movementStatus.movementStatusLocationX) / 100);

        // Add Padding to left of the screen
        if (xAxis <= 4) {
            xAxis = 4;
        }

        // Add Padding to top of the screen
        if (yAxis <= 4) {
            yAxis = 4;
        }

        // Draw Info
        context.drawTextWithShadow(this.renderer, text, xAxis, yAxis, config.uiConfig.textColor);
    }
}
