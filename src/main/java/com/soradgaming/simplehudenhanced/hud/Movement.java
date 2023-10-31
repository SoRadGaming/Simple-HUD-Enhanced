package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.utli.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class Movement {
    private final MinecraftClient client;
    private final TextRenderer renderer;
    private final SimpleHudEnhancedConfig config;
    private final MatrixStack matrixStack;

    public Movement(MatrixStack matrixStack, SimpleHudEnhancedConfig config) {
        this.client = MinecraftClient.getInstance();
        this.renderer = client.textRenderer;
        this.config = config;
        this.matrixStack = matrixStack;
    }

    public void init(GameInfo GameInformation) {
        if (config.movementStatus.toggleSwimmingStatus && GameInformation.isPlayerSwimming()) {
            draw("text.hud.simplehudenhanced.swimming");
        } else if (config.movementStatus.toggleFlyingStatus && GameInformation.isPlayerFlying()) {
            draw("text.hud.simplehudenhanced.flying");
        } else if (config.movementStatus.toggleSneakStatus && GameInformation.isPlayerSneaking()) {
            draw("text.hud.simplehudenhanced.sneaking");
        } else if (config.movementStatus.toggleSprintStatus && GameInformation.isPlayerSprinting()) {
            draw("text.hud.simplehudenhanced.sprinting");
        }
    }

    // Draw the movement status on the screen
    private void draw(String textKey) {
        final String text = Utilities.translatable(textKey).getString();
        float Scale = (float) config.movementStatus.textScale / 100;

        // Screen Manager
        ScreenManager screenManager = new ScreenManager(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
        screenManager.setPadding(4);
        int xAxis = screenManager.calculateXAxis(config.movementStatus.movementStatusLocationX, Scale, this.renderer.getWidth(text));
        int yAxis = screenManager.calculateYAxis(this.renderer.fontHeight, 1, config.movementStatus.movementStatusLocationY, Scale);
        screenManager.setScale(matrixStack, Scale);

        // Draw Info
        this.renderer.drawWithShadow(this.matrixStack, text, xAxis, yAxis, config.uiConfig.textColor);

        screenManager.resetScale(matrixStack);
    }
}
