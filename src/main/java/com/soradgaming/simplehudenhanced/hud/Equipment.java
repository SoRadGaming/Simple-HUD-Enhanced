package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.config.EquipmentOrientation;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.utli.Colours;
import com.soradgaming.simplehudenhanced.utli.TrinketAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.soradgaming.simplehudenhanced.utli.Utilities.getModName;

public class Equipment {
    private final MinecraftClient client;
    private final TextRenderer textRenderer;
    private final ItemRenderer itemRenderer;
    private ClientPlayerEntity player;
    private final SimpleHudEnhancedConfig config;
    private final MatrixStack matrixStack;

    // Equipment Info
    private List<EquipmentInfoStack> equipmentInfo;

    public Equipment(MatrixStack matrixStack, SimpleHudEnhancedConfig config) {
        this.client = MinecraftClient.getInstance();
        this.textRenderer = client.textRenderer;
        this.itemRenderer = client.getItemRenderer();
        this.config = config;
        this.matrixStack = matrixStack;

        // Get the player
        if (this.client.player != null) {
            this.player = this.client.player;
        } else {
            Logger logger = LogManager.getLogger(getModName());
            logger.error("Player is null", new Exception("Player is null"));
        }
    }

    public void init() {
        // Trinkets or Normal
        TrinketAccessor trinketData = new TrinketAccessor(this.player, config);
        equipmentInfo = trinketData.getEquipmentInfo();

        // Remove Air Blocks from the list
        equipmentInfo.removeIf(equipment -> equipment.getItem().getItem().equals(Blocks.AIR.asItem()));

        // Remove Items with 0 count
        equipmentInfo.removeIf(equipment -> equipment.getItem().getCount() == 0);

        // Check showNonTools
        if (!config.equipmentStatus.showNonTools) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().getItem().getMaxDamage() == 0);
        }

        if (config.equipmentStatus.Durability.showDurability) {
            getDurability();
        }

        // Check Config for Item Slot Disabled
        if (!config.equipmentStatus.slots.Head) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().equals(this.player.getInventory().getArmorStack(3)));
        }
        if (!config.equipmentStatus.slots.Body) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().equals(this.player.getInventory().getArmorStack(2)));
        }
        if (!config.equipmentStatus.slots.Legs) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().equals(this.player.getInventory().getArmorStack(1)));
        }
        if (!config.equipmentStatus.slots.Boots) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().equals(this.player.getInventory().getArmorStack(0)));
        }
        if (!config.equipmentStatus.slots.OffHand) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().equals(this.player.getOffHandStack()));
        }
        if (!config.equipmentStatus.slots.MainHand) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().equals(this.player.getMainHandStack()));
        }

        // Draw Items
        draw();
    }

    private void getDurability() {
        // Get each Items String and Color
        for (EquipmentInfoStack index : equipmentInfo) {
            ItemStack item = index.getItem();

            // Check Config for Item Durability Disabled
            if (isItemDurabilityDisabled(item)) {
                index.setText("");
                continue;
            }

            // Check if item has durability (Tools, Weapons, Armor) or not (Blocks, Food, etc.)
            if (item.getMaxDamage() != 0) {
                int currentDurability = item.getMaxDamage() - item.getDamage();

                // Draw Durability
                if (config.equipmentStatus.Durability.showDurabilityAsBar) {
                    index.setText("");
                } else if (config.equipmentStatus.Durability.showDurabilityAsPercentage)  {
                    index.setText(String.format("%s%%", (currentDurability * 100) / item.getMaxDamage()));
                } else if (config.equipmentStatus.Durability.showTotalCount) {
                    index.setText(String.format("%s/%s", currentDurability, item.getMaxDamage()));
                } else {
                    index.setText(String.format("%s", currentDurability));
                }

                if (config.equipmentStatus.Durability.showColour || config.equipmentStatus.Durability.showDurabilityAsBar) {
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

                // Check for 1
                if (this.player.getInventory().count(item.getItem()) == 1) {
                    index.setText("");
                }

            }
        }
    }

    private boolean isItemDurabilityDisabled(ItemStack item) {
        if (item.equals(this.player.getMainHandStack())) {
            return !config.equipmentStatus.Durability.slots.MainHand;
        } else if (item.equals(this.player.getOffHandStack())) {
            return !config.equipmentStatus.Durability.slots.OffHand;
        } else if (item.equals(this.player.getInventory().getArmorStack(0))) {
            return !config.equipmentStatus.Durability.slots.Boots;
        } else if (item.equals(this.player.getInventory().getArmorStack(1))) {
            return !config.equipmentStatus.Durability.slots.Legs;
        } else if (item.equals(this.player.getInventory().getArmorStack(2))) {
            return !config.equipmentStatus.Durability.slots.Body;
        } else if (item.equals(this.player.getInventory().getArmorStack(3))) {
            return !config.equipmentStatus.Durability.slots.Head;
        } else {
            return false;
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
                BoxWidth = this.textRenderer.getWidth(s);
            }
        }


        // Screen Size Calculations
        int configX = config.equipmentStatus.equipmentStatusLocationX;
        int configY = config.equipmentStatus.equipmentStatusLocationY;
        float Scale = (float) config.equipmentStatus.textScale / 100;
        int lineHeight = 16;
        if (config.equipmentStatus.Durability.showDurabilityAsBar) {
            lineHeight = 18;
        }

        // Screen Manager
        ScreenManager screenManager = new ScreenManager(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
        screenManager.setPadding(4);
        int xAxis = screenManager.calculateXAxis(configX, Scale, (BoxWidth + 16));
        int yAxis = screenManager.calculateYAxis(lineHeight, equipmentInfo.size(), configY, Scale);
        if (config.equipmentStatus.equipmentOrientation == EquipmentOrientation.Horizontal) {
            int total = 0;
            for (EquipmentInfoStack index : equipmentInfo) {
                int lineLength = this.textRenderer.getWidth(index.getText());
                total += lineLength;
            }
            xAxis = screenManager.calculateXAxis(configX, Scale, total + (24 * equipmentInfo.size()) - 4);
            yAxis = screenManager.calculateYAxis(lineHeight, 1, configY, Scale);
        }
        screenManager.setScale(matrixStack, Scale);

        // Draw All Items on Screen
        for (EquipmentInfoStack index : equipmentInfo) {
            ItemStack item = index.getItem();

            if (config.equipmentStatus.equipmentOrientation == EquipmentOrientation.Vertical) {
                if (configX >= 50) {
                    int lineLength = this.textRenderer.getWidth(index.getText());
                    int offset = (BoxWidth - lineLength);
                    if (!drawDurabilityBar(xAxis + BoxWidth, yAxis, index, item)) {
                        if (config.equipmentStatus.Durability.showDurabilityAsBar) {
                            this.textRenderer.drawWithShadow(matrixStack, index.getText(), xAxis + offset - 4, yAxis + 6, index.getColor());
                        } else {
                            this.textRenderer.drawWithShadow(matrixStack, index.getText(), xAxis + offset - 4, yAxis + 4, index.getColor());
                        }
                    }
                    this.itemRenderer.renderInGuiWithOverrides(this.matrixStack, item, xAxis + BoxWidth, yAxis);
                } else {
                    if (!drawDurabilityBar(xAxis, yAxis, index, item)) {
                        if (config.equipmentStatus.Durability.showDurabilityAsBar) {
                            this.textRenderer.drawWithShadow(matrixStack, index.getText(), xAxis + 16 + 4, yAxis + 6, index.getColor());
                        } else {
                            this.textRenderer.drawWithShadow(matrixStack, index.getText(), xAxis + 16 + 4, yAxis + 4, index.getColor());
                        }
                    }
                    this.itemRenderer.renderInGuiWithOverrides(this.matrixStack, item, xAxis, yAxis);
                }
                yAxis += lineHeight;
            } else {
                if (!drawDurabilityBar(xAxis, yAxis, index, item)) {
                    if (config.equipmentStatus.Durability.showDurabilityAsBar) {
                        this.textRenderer.drawWithShadow(matrixStack, index.getText(), xAxis + 16 + 4, yAxis + 6, index.getColor());
                    } else {
                        this.textRenderer.drawWithShadow(matrixStack, index.getText(), xAxis + 16 + 4, yAxis + 4, index.getColor());
                    }
                }
                this.itemRenderer.renderInGuiWithOverrides(this.matrixStack, item, xAxis, yAxis);
                int lineLength = this.textRenderer.getWidth(index.getText());
                xAxis += lineLength + 16 + 4 + 4;
            }
        }

        screenManager.resetScale(matrixStack);
    }

    private boolean drawDurabilityBar(int xAxis, int yAxis, EquipmentInfoStack index, ItemStack item) {
        if (config.equipmentStatus.Durability.showDurabilityAsBar && item.getMaxDamage() != 0) {
            // Check for 100% durability
            if (item.getDamage() == 0) {
                return true;
            }

            // Calculate durability ratio
            float durabilityRatio = (float) item.getDamage() / item.getMaxDamage();

            // Calculate the length of the durability bar
            int barLength = (int) (durabilityRatio * 16);

            // Draw the durability bar
            DrawableHelper.fill(matrixStack, xAxis + barLength, yAxis + 16, xAxis + 16, yAxis + 17, 0x80000000);// 0x80000000
            DrawableHelper.fill(matrixStack, xAxis, yAxis + 16, xAxis + 16 - barLength, yAxis + 17, index.getColor()); // 0xFF00FF00
            return true;
        }
        return false;
    }
}
