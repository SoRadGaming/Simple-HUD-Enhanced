package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.utli.Colours;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            Exception e = new Exception("Player is null");
            e.printStackTrace();
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
        int lineHeight = this.renderer.fontHeight + 8;
        int yAxis = (this.client.getWindow().getScaledHeight() - (lineHeight * equipmentInfo.size())) * (config.equipmentStatus.equipmentStatusLocationY) / 100;
        int xAxis = ((this.client.getWindow().getScaledWidth() - 8) - (BoxWidth + 20)) * configX / 100;

        // Add Padding to left of the screen
        if (xAxis <= 4) {
            xAxis = 4;
        }

        // Add Padding to top of the screen
        if (yAxis <= 4) {
            yAxis = 4;
        }

        // Draw All Items on Screen
        for (EquipmentInfoStack index : equipmentInfo) {
            ItemStack item = index.getItem();

            if (configX >= 50) {
                int lineLength = this.renderer.getWidth(index.getText());
                int offset = (BoxWidth - lineLength);

                this.context.drawTextWithShadow(this.renderer, index.getText(), xAxis + offset + 2, yAxis, index.getColor());
                this.context.drawItem(item, xAxis + BoxWidth + 6, yAxis - 5);
            } else {
                this.context.drawTextWithShadow(this.renderer, index.getText(), xAxis + 20, yAxis, index.getColor());
                this.context.drawItem(item, xAxis, yAxis - 5);
            }
            yAxis += lineHeight;
        }
    }
}
