package com.soradgaming.simplehudenhanced.cache;

import com.soradgaming.simplehudenhanced.config.EquipmentOrientation;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.hud.EquipmentInfoStack;
import com.soradgaming.simplehudenhanced.hud.ScreenManager;
import com.soradgaming.simplehudenhanced.utli.TrinketAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

import java.util.List;

public class EquipmentCache {
    private static EquipmentCache instance; // Singleton instance
    private List<EquipmentInfoStack> equipmentInfo;
    private int longestString = 0;
    private final SimpleHudEnhancedConfig config;
    private ClientPlayerEntity player;


    private EquipmentCache(SimpleHudEnhancedConfig config) {
        this.config = config;
        // Register Events
        registerEvent();
    }

    public static EquipmentCache getInstance(SimpleHudEnhancedConfig config) {
        if(instance == null) {
            instance = new EquipmentCache(config);
        }
        return instance;
    }

    private void registerEvent() {
        UpdateCacheEvent.EVENT.register((cache) -> {
            if (cache == Cache.EQUIPMENT) {
                // Run Code on Event
                setCacheValid(false);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });
    }

    private void updateCache(ClientPlayerEntity player) {
        createEquipment(player);
        calculateLongestString();
        calculateScreen();
        setCacheValid(true);
    }

    public List<EquipmentInfoStack> getEquipmentInfo(ClientPlayerEntity player) {
        if (!isCacheValid() || isTrinketInstalled()) {
            updateCache(player);
        }
        return equipmentInfo;
    }

    private void createEquipment(ClientPlayerEntity player) {
        // Save Player for later use
        this.player = player;

        // Trinkets or Normal
        TrinketAccessor trinketData = new TrinketAccessor(player, config);
        equipmentInfo = trinketData.getEquipmentInfo();

        // Remove Air Blocks from the list
        equipmentInfo.removeIf(equipment -> equipment.getItem().getItem().equals(Blocks.AIR.asItem()));

        // Remove Items with 0 count
        equipmentInfo.removeIf(equipment -> equipment.getItem().getCount() == 0);

        // Check showNonTools
        if (!config.equipmentStatus.showNonTools) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().getMaxDamage() == 0);
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
                if (config.equipmentStatus.Durability.showDurabilityAsPercentage)  {
                    index.setText(String.format("%s%%", (currentDurability * 100) / item.getMaxDamage()));
                } else if (config.equipmentStatus.Durability.showTotalCount) {
                    index.setText(String.format("%s/%s", currentDurability, item.getMaxDamage()));
                } else {
                    index.setText(String.format("%s", currentDurability));
                }

                if (config.equipmentStatus.Durability.showColour && item.getDamage() != 0) {
                    index.setColor(item.getItemBarColor());
                } else {
                    index.setColor(config.uiConfig.textColor);
                }

            } else {
                // Draw Count - Update to Check if player is holding 1 or more of the item (including inventory)
                if (config.equipmentStatus.showCount && this.player.getInventory().count(item.getItem()) > 1) {
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

    public int getLongestString(ClientPlayerEntity player) {
        if (!isCacheValid()) {
            updateCache(player);
        }
        return longestString;
    }

    private void calculateLongestString() {
        // Get the longest string in the array
        int longestString = 0;
        int BoxWidth = 0;
        for (EquipmentInfoStack index : equipmentInfo) {
            String s = index.getText();
            if (s.length() > longestString) {
                longestString = s.length();
                BoxWidth = MinecraftClient.getInstance().textRenderer.getWidth(s);
            }
        }
        this.longestString = BoxWidth;
    }

    private boolean isCacheValid = false;

    private boolean isCacheValid() {
        return isCacheValid;
    }

    public void setCacheValid(boolean cacheValid) {
        isCacheValid = cacheValid;
    }

    /***************************
           ScreenManager
     ***************************/
    private ScreenManager screenManager;

    public ScreenManager getScreenManager(ClientPlayerEntity player) {
        if (!isCacheValid()) {
            updateCache(player);
        }
        return screenManager;
    }

    private void calculateScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer renderer = client.textRenderer;

        // Screen Size Calculations
        int configX = config.equipmentStatus.equipmentStatusLocationX;
        int configY = config.equipmentStatus.equipmentStatusLocationY;
        float Scale = (float) config.equipmentStatus.textScale / 100;
        int lineHeight = 16;

        // Screen Manager
        screenManager = new ScreenManager(client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
        screenManager.setPadding(4);
        int xAxis = screenManager.calculateXAxis(configX, Scale, (this.longestString + 16));
        int yAxis = screenManager.calculateYAxis(lineHeight, equipmentInfo.size(), configY, Scale);
        if (config.equipmentStatus.equipmentOrientation == EquipmentOrientation.Horizontal) {
            int total = 0;
            for (EquipmentInfoStack index : equipmentInfo) {
                int lineLength = renderer.getWidth(index.getText());
                total += lineLength;
            }
            xAxis = screenManager.calculateXAxis(configX, Scale, total + (24 * equipmentInfo.size()) - 4);
            yAxis = screenManager.calculateYAxis(lineHeight, 1, configY, Scale);
        }

        screenManager.saveXAxis(xAxis);
        screenManager.saveYAxis(yAxis);
        screenManager.saveScale(Scale);
    }

    private boolean isTrinketInstalled() {
        return FabricLoader.getInstance().isModLoaded("trinkets");
    }
}
