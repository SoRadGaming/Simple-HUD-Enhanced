package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.cache.EquipmentCache;
import com.soradgaming.simplehudenhanced.config.EquipmentAlignment;
import com.soradgaming.simplehudenhanced.config.EquipmentOrientation;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.soradgaming.simplehudenhanced.utli.Utilities.getModName;

public class Equipment {
    private final MinecraftClient client;
    private final TextRenderer renderer;
    private ClientPlayerEntity player;
    private final SimpleHudEnhancedConfig config;
    private final DrawContext context;
    private final EquipmentCache cache;

    // Equipment Info
    private List<EquipmentInfoStack> equipmentInfo;

    public Equipment(DrawContext context, SimpleHudEnhancedConfig config, EquipmentCache equipmentCache) {
        this.client = MinecraftClient.getInstance();
        this.renderer = client.textRenderer;
        this.config = config;
        this.context = context;
        this.cache = equipmentCache;

        // Get the player
        if (this.client.player != null) {
            this.player = this.client.player;
        } else {
            Logger logger = LogManager.getLogger(getModName());
            logger.error("Player is null", new Exception("Player is null"));
        }
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


        screenManager.setScale(context, Scale);

        // Draw All Items on Screen
        boolean isHorizontal = config.equipmentStatus.equipmentOrientation == EquipmentOrientation.Horizontal;
        boolean isOnRight = configX >= 50;
        boolean isRightAligned = config.equipmentStatus.equipmentAlignment == EquipmentAlignment.Right || (config.equipmentStatus.equipmentAlignment == EquipmentAlignment.Auto && isOnRight);
        // Loop all items
        for (EquipmentInfoStack index : equipmentInfo) {
            ItemStack item = index.getItem();

            if (isRightAligned) {
                int lineLength = this.renderer.getWidth(index.getText());
                int offset = (BoxWidth - lineLength);
                this.context.drawTextWithShadow(this.renderer, index.getText(), xAxis + offset + (isHorizontal? (-offset) : (isOnRight ? -4 : 0)), yAxis + 4, index.getColor());
                int x = xAxis + BoxWidth + 4 + (isHorizontal ? (-BoxWidth + lineLength) : (isOnRight ? -4 : 0));
                this.context.drawItem(item, x, yAxis);
                drawDurabilityBar(x, yAxis, item);
            } else {
                this.context.drawTextWithShadow(this.renderer, index.getText(), xAxis + 16 + 4 + (isOnRight ? (isHorizontal? 0 : -4) : 0), yAxis + 4, index.getColor());
                int x = xAxis + (isOnRight ? (isHorizontal ? 0 : -4) : 0);
                this.context.drawItem(item, x, yAxis);
                drawDurabilityBar(x, yAxis, item);
            }
            if (isHorizontal) {
                int lineLength = this.renderer.getWidth(index.getText());
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
            if (item.getDamage() == 0) {
                return;
            }

            int i = item.getItemBarStep();
            int j = item.getItemBarColor();
            int k = xAxis + 2;
            int l = yAxis + 13;
            this.context.fill(k, l, k + 13, l + 2, 200, -16777216);
            this.context.fill(k, l, k + i, l + 1, 200, j | -16777216);
        }
    }
}
