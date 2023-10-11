package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.utli.Colours;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.soradgaming.simplehudenhanced.utli.Utilities.getModName;

public class Equipment extends HUD {
    private final MinecraftClient client;
    private final TextRenderer renderer;
    private ClientPlayerEntity player;
    private final SimpleHudEnhancedConfig config;
    private final DrawContext context;

    // Equipment Info
    private List<EquipmentInfoStack> equipmentInfo;

    public Equipment(DrawContext context, SimpleHudEnhancedConfig config) {
        super(MinecraftClient.getInstance());

        this.client = MinecraftClient.getInstance();
        this.renderer = client.textRenderer;
        this.config = config;
        this.context = context;

        // Get the player
        if (this.client.player != null) {
            this.player = this.client.player;
        } else {
            Logger logger = LogManager.getLogger(getModName());
            logger.error("Player is null", new Exception("Player is null"));
        }
    }

    public void init() {
        // Get Armour and Hand Item from Inventory and Offhand
        equipmentInfo = new ArrayList<>(
                Arrays.asList(
                        new EquipmentInfoStack(this.player.getInventory().getArmorStack(3)),
                        new EquipmentInfoStack(this.player.getInventory().getArmorStack(2)),
                        new EquipmentInfoStack(this.player.getInventory().getArmorStack(1)),
                        new EquipmentInfoStack(this.player.getInventory().getArmorStack(0)),
                        new EquipmentInfoStack(this.player.getOffHandStack()),
                        new EquipmentInfoStack(this.player.getMainHandStack())
                )
        );

        // Remove Air Blocks from the list
        equipmentInfo.removeIf(equipment -> equipment.getItem().getItem().equals(Blocks.AIR.asItem()));

        // Check showNonTools
        if (!config.equipmentStatus.showNonTools) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().getItem().getMaxDamage() == 0);
        }

        if (config.equipmentStatus.showDurability) {
            // Draw Durability
            getDurability();
        }

        // Draw Items
        draw();
    }

    private void getDurability() {
        // Get each Items String and Color
        for (EquipmentInfoStack index : equipmentInfo) {
            ItemStack item = index.getItem();

            // Check if item has durability (Tools, Weapons, Armor) or not (Blocks, Food, etc.)
            if (item.getMaxDamage() != 0) {
                int currentDurability = item.getMaxDamage() - item.getDamage();

                // Draw Durability
                index.setText(String.format("%s/%s", currentDurability, item.getMaxDamage()));

                // Default Durability Color
                if (currentDurability <= (item.getMaxDamage()) / 4) {
                    index.setColor(Colours.lightRed);
                } else if (currentDurability <= (item.getMaxDamage() / 2.5)) {
                    index.setColor(Colours.lightOrange);
                } else if (currentDurability <= (item.getMaxDamage() / 1.5)) {
                    index.setColor(Colours.lightYellow);
                } else if (currentDurability < item.getMaxDamage()) {
                    index.setColor(Colours.lightGreen);
                } else {
                    index.setColor(config.uiConfig.textColor);
                }
            } else {
                // Draw Count
                if (config.equipmentStatus.showCount) {
                    // Check if player is holding all the item
                    if (this.player.getInventory().count(item.getItem()) == item.getCount()) {
                        index.setText(String.valueOf(item.getCount()));
                    } else {
                        index.setText((item.getCount() + " (" + this.player.getInventory().count(item.getItem()) + ")"));
                    }
                } else {
                    index.setText("");
                }

            }
        }
    }

    private void draw() {
        // Get the longest string in the array
        int longestString = 0;
        int BoxWidth = 0;
        for (EquipmentInfoStack index : equipmentInfo) {
            String s = index.getText();
            if (s.length() > longestString) {
                longestString = s.length();
                BoxWidth = this.renderer.getWidth(s);
            }
        }


        // Screen Size Calculations
        int configX = config.equipmentStatus.equipmentStatusLocationX;
        int configY = config.equipmentStatus.equipmentStatusLocationY;
        float Scale = (float) config.equipmentStatus.textScale / 100;
        int lineHeight = 16;

        // Screen Manager
        ScreenManager screenManager = new ScreenManager(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
        screenManager.setPadding(4);
        int xAxis = screenManager.calculateXAxis(configX, Scale, (BoxWidth + 16));
        int yAxis = screenManager.calculateYAxis(lineHeight, equipmentInfo.size(), configY, Scale);
        screenManager.setScale(context, Scale);

        // Draw All Items on Screen
        for (EquipmentInfoStack index : equipmentInfo) {
            ItemStack item = index.getItem();

            if (configX >= 50) {
                int lineLength = this.renderer.getWidth(index.getText());
                int offset = (BoxWidth - lineLength);

                this.context.drawTextWithShadow(this.renderer, index.getText(), xAxis + offset - 4, yAxis + 4, index.getColor());
                this.context.drawItem(item, xAxis + BoxWidth, yAxis);
            } else {
                this.context.drawTextWithShadow(this.renderer, index.getText(), xAxis + 16 + 4, yAxis + 4, index.getColor());
                this.context.drawItem(item, xAxis, yAxis);
            }
            yAxis += lineHeight;
        }

        screenManager.resetScale(context);
    }
}
