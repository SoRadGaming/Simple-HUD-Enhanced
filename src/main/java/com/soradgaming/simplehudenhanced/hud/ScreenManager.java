package com.soradgaming.simplehudenhanced.hud;

import net.minecraft.client.util.math.MatrixStack;

public class ScreenManager {
    private final int screenWidth;
    private final int screenHeight;
    private int padding;

    public ScreenManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.padding = 0;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public int calculateXAxis(int configX, double scale, int lineWidth) {
        int adjustedWidth = (int) Math.round(screenWidth - (padding) - (lineWidth * scale));
        int calculatedValue = (int) Math.round(adjustedWidth / scale * configX / 100);

        if (scale < 1) {
            int adjustedScreenWidth = (int) Math.round(screenWidth * scale);
            int maximumX = padding;
            int minimumX = Math.min(screenWidth - adjustedScreenWidth - padding, maximumX);
            return Math.max(Math.max(calculatedValue, minimumX), maximumX);
        } else {
            int adjustedLineWidth = (int) Math.round(lineWidth * scale);
            return Math.min(Math.max(calculatedValue, padding), screenWidth - padding - adjustedLineWidth);
        }
    }

    public int calculateYAxis(int lineHeight, int size, int configY, double scale) {
        int adjustedHeight = (int) Math.round(screenHeight - padding - (lineHeight * size) * scale);
        int calculatedValue = (int) Math.round(adjustedHeight / scale * configY / 100);

        if (scale < 1) {
            return Math.max(calculatedValue, padding);
        } else {
            int adjustedLineHeight = (int) Math.round((lineHeight * size) * scale);
            return Math.min(Math.max(calculatedValue, padding), screenHeight - adjustedLineHeight - padding);
        }
    }

    public void setScale(MatrixStack matrix, float scale) {
        // Change Matrix Stack to draw on the screen
        matrix.push();
        matrix.scale(scale, scale, scale);
    }

    public void resetScale(MatrixStack matrix) {
        // Change Matrix Stack back to normal
        matrix.pop();
    }
}
