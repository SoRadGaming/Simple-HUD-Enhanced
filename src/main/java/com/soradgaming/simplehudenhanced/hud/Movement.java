package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.utli.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class Movement extends HUD {
    private final MinecraftClient client;
    private final TextRenderer renderer;
    private final SimpleHudEnhancedConfig config;
    private final DrawContext context;

    public Movement(DrawContext context, SimpleHudEnhancedConfig config) {
        super(MinecraftClient.getInstance());

        this.client = MinecraftClient.getInstance();
        this.renderer = client.textRenderer;
        this.config = config;
        this.context = context;
    }

    public void init(GameInfo GameInformation) {
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
