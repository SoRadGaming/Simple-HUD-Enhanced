package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.MathHelper;

public class StatusEffectBarRenderer {
    private static SimpleHudEnhancedConfig config;
    public static void render(MatrixStack matrixStack, StatusEffectInstance effect, int x, int y, int width, int height) {
        config = AutoConfig.getConfigHolder(SimpleHudEnhancedConfig.class).getConfig();

        if (!config.toggleEffectsStatus) return;

        float progress = (float) effect.getDuration() / ((StatusEffectInstanceDuck) effect).statusEffectBars_getMaxDuration();
        float progress1 = calculateProgress(progress, 0.25f);
        float progress2 = calculateProgress(progress, 0.5f);
        float progress3 = calculateProgress(progress, 0.75f);
        float progress4 = calculateProgress(progress, 1f);



        drawVerticalBar(x, y, 2, 3, height - 3, progress4, matrixStack, effect);
        drawHorizontalBar(x, y, width - 3, 3,2, progress3, matrixStack, effect);
        drawVerticalBar(x, y, width - 3,height - 3,  3, progress2, matrixStack, effect);
        drawHorizontalBar(x, y, 3 ,width - 3, height - 3, progress1, matrixStack, effect);
    }

    private static float calculateProgress(float value, float threshold) {
        if (value >= threshold) {
            return 1;
        } else if (value <= threshold - 0.25f) {
            return 0;
        } else {
            return (value - threshold + 0.25f) / 0.25f;
        }
    }

    private static void drawVerticalBar(int x, int y, int startX, int startY, int endY, float progress, MatrixStack matrixStack, StatusEffectInstance effect) {
        int middleX = startX + 1;
        int middleY = Math.round(MathHelper.lerp(progress, startY, endY));
        int endX = startX;

        startX += x;
        middleX += x;
        endX += x;
        startY += y;
        middleY += y;
        endY += y;

        DrawableHelper.fill(matrixStack, startX, startY, middleX, middleY, config.getColor(effect));
        DrawableHelper.fill(matrixStack, middleX, middleY, endX, endY, config.effectsStatus.backgroundColor);
    }

    private static void drawHorizontalBar(int x, int y, int startX, int endX, int startY,float progress, MatrixStack matrixStack, StatusEffectInstance effect) {
        int middleY = startY + 1;
        int endY = startY;
        int middleX = Math.round(MathHelper.lerp(progress, startX, endX));

        startX += x;
        middleX += x;
        endX += x;
        startY += y;
        middleY += y;
        endY += y;

        DrawableHelper.fill(matrixStack, startX, startY, middleX, middleY, config.getColor(effect));
        DrawableHelper.fill(matrixStack, middleX, middleY, endX, endY, config.effectsStatus.backgroundColor);
    }
}
