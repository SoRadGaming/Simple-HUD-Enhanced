package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.cache.EquipmentCache;
import com.soradgaming.simplehudenhanced.config.EquipmentAlignment;
import com.soradgaming.simplehudenhanced.config.EquipmentOrientation;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import java.util.List;

public class Equipment {
    private final TextRenderer textRenderer;
    private final ItemRenderer itemRenderer;
    private final ClientPlayerEntity player;
    private final SimpleHudEnhancedConfig config;
    private final MatrixStack matrixStack;
    private final EquipmentCache cache;

    public Equipment(MatrixStack matrixStack, SimpleHudEnhancedConfig config, EquipmentCache equipmentCache) {
        MinecraftClient client = MinecraftClient.getInstance();
        this.textRenderer = client.textRenderer;
        this.itemRenderer = client.getItemRenderer();
        this.config = config;
        this.matrixStack = matrixStack;
        this.cache = equipmentCache;

        // Get the player
        this.player = MinecraftClient.getInstance().player;
    }

    public void init() {
        // Draw Items
        draw(cache.getEquipmentInfo(player));
    }

    private void draw(List<EquipmentInfoStack> equipmentInfo) {
        int BoxWidth = cache.getLongestString(this.player);
        ScreenManager screenManager = cache.getScreenManager(player);
        int xAxis = screenManager.getXAxis();
        int yAxis = screenManager.getYAxis();
        float Scale = screenManager.getScale();
        int lineHeight = 16;
        int configX = config.equipmentStatus.equipmentStatusLocationX;


        screenManager.setScale(matrixStack, Scale);

        // Draw All Items on Screen
        boolean isHorizontal = config.equipmentStatus.equipmentOrientation == EquipmentOrientation.Horizontal;
        boolean isOnRight = configX >= 50;
        boolean isRightAligned = config.equipmentStatus.equipmentAlignment == EquipmentAlignment.Right || (config.equipmentStatus.equipmentAlignment == EquipmentAlignment.Auto && isOnRight);
        // Loop all items
        for (EquipmentInfoStack index : equipmentInfo) {
            ItemStack item = index.getItem();

            if (isRightAligned) {
                int lineLength = this.textRenderer.getWidth(index.getText());
                int offset = (BoxWidth - lineLength);
                this.textRenderer.drawWithShadow(matrixStack, index.getText(), xAxis + offset + (isHorizontal? (-offset) : (isOnRight ? -4 : 0)), yAxis + 4, index.getColor());
                int x = xAxis + BoxWidth + 4 + (isHorizontal ? (-BoxWidth + lineLength) : (isOnRight ? -4 : 0));
                this.itemRenderer.renderInGuiWithOverrides(matrixStack, item, x, yAxis);
                drawDurabilityBar(screenManager, x, yAxis, item);
            } else {
                this.textRenderer.drawWithShadow(matrixStack, index.getText(), xAxis + 16 + 4 + (isOnRight ? (isHorizontal? 0 : -4) : 0), yAxis + 4, index.getColor());
                int x = xAxis + (isOnRight ? (isHorizontal ? 0 : -4) : 0);
                this.itemRenderer.renderInGuiWithOverrides(matrixStack, item, x, yAxis);
                drawDurabilityBar(screenManager, x, yAxis, item);
            }
            if (isHorizontal) {
                int lineLength = this.textRenderer.getWidth(index.getText());
                xAxis += lineLength + 4 + 16 + 4;
            } else {
                yAxis += lineHeight;
            }
        }

        screenManager.resetScale(matrixStack);
    }

    private void drawDurabilityBar(ScreenManager screenManager, int xAxis, int yAxis, ItemStack item) {
        if (config.equipmentStatus.Durability.showDurabilityAsBar && item.getMaxDamage() != 0) {
            // Check for 100% durability
            if (item.getDamage() == 0) {
                return;
            }

            screenManager.zSet(matrixStack, 200.0F);
            int i = item.getItemBarStep();
            int j = item.getItemBarColor();
            int k = xAxis + 2;
            int l = yAxis + 13;
            DrawableHelper.fill(matrixStack, k, l, k + 13, l + 2, -16777216);
            DrawableHelper.fill(matrixStack, k, l, k + i, l + 1, j | -16777216);
            screenManager.zRevert(matrixStack);
        }
    }
}
