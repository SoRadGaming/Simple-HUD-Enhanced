package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.cache.EquipmentCache;
import com.soradgaming.simplehudenhanced.config.EquipmentAlignment;
import com.soradgaming.simplehudenhanced.config.EquipmentOrientation;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class Equipment {
    private final Font renderer;
    private final SimpleHudEnhancedConfig config;
    private final GuiGraphics context;
    private final EquipmentCache cache;

    public Equipment(GuiGraphics context, SimpleHudEnhancedConfig config, EquipmentCache equipmentCache) {
        this.renderer = Minecraft.getInstance().font;
        this.config = config;
        this.context = context;
        this.cache = equipmentCache;
    }

    public void init() {
        // Draw Items
        draw(cache.getEquipmentInfo());
    }

    private void draw(List<EquipmentInfoStack> equipmentInfo) {
        int BoxWidth = cache.getLongestString();
        ScreenManager screenManager = cache.getScreenManager();
        int xAxis = screenManager.getXAxis();
        int yAxis = screenManager.getYAxis();
        float Scale = screenManager.getScale();
        int lineHeight = 16;
        int configX = config.equipmentStatus.equipmentStatusLocationX;


        screenManager.setScale(context, Scale);

        // Draw All Items on Screen
        boolean isHorizontal = config.equipmentStatus.equipmentOrientation == EquipmentOrientation.Horizontal;
        boolean isOnRight = configX >= 50;
        boolean isRightAligned = config.equipmentStatus.equipmentAlignment == EquipmentAlignment.Right || (config.equipmentStatus.equipmentAlignment == EquipmentAlignment.Auto && isOnRight);
        // Loop all items
        for (EquipmentInfoStack index : equipmentInfo) {
            ItemStack item = index.getItem();

            if (isRightAligned) {
                int lineLength = this.renderer.width(index.getText());
                int offset = (BoxWidth - lineLength);
                this.context.drawString(this.renderer, index.getText(), xAxis + offset + (isHorizontal? (-offset) : (isOnRight ? -4 : 0)), yAxis + 4, index.getColor());
                int x = xAxis + BoxWidth + 4 + (isHorizontal ? (-BoxWidth + lineLength) : (isOnRight ? -4 : 0));
                this.context.renderItem(item, x, yAxis);
                drawDurabilityBar(x, yAxis, item);
            } else {
                this.context.drawString(this.renderer, index.getText(), xAxis + 16 + 4 + (isOnRight ? (isHorizontal? 0 : -4) : 0), yAxis + 4, index.getColor());
                int x = xAxis + (isOnRight ? (isHorizontal ? 0 : -4) : 0);
                this.context.renderItem(item, x, yAxis);
                drawDurabilityBar(x, yAxis, item);
            }
            if (isHorizontal) {
                int lineLength = this.renderer.width(index.getText());
                xAxis += lineLength + 4 + 16 + 4;
            } else {
                yAxis += lineHeight;
            }
        }

        screenManager.resetScale(context);
    }

    private void drawDurabilityBar(int xAxis, int yAxis, ItemStack item) {
        if (config.equipmentStatus.Durability.showDurabilityAsBar && item.getMaxDamage() != 0) {
            // Check for 100% durability
            if (item.getDamageValue() == 0) {
                return;
            }

            int i = item.getBarWidth();
            int j = item.getBarColor();
            int k = xAxis + 2;
            int l = yAxis + 13;
            this.context.fill(k, l, k + 13, l + 2, 200, -16777216);
            this.context.fill(k, l, k + i, l + 1, 200, j | -16777216);
        }
    }
}
