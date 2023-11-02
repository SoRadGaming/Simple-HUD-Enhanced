package com.soradgaming.simplehudenhanced.cache;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.hud.EquipmentInfoStack;
import com.soradgaming.simplehudenhanced.utli.Colours;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EquipmentCache {
    private static EquipmentCache instance;
    private List<EquipmentInfoStack> equipmentInfo;
    private final SimpleHudEnhancedConfig config;

    public int cacheUpdates = 0;

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
                cacheUpdates++;
                setCacheValid(false);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });
    }

    private void updateCache(ClientPlayerEntity player) {
        createEquipment(player);
        setCacheValid(true);
    }

    public List<EquipmentInfoStack> getEquipmentInfo(ClientPlayerEntity player) {
        if (!isCacheValid()) {
            updateCache(player);
        }
        return equipmentInfo;
    }

    private void createEquipment(ClientPlayerEntity player) {
        // Get Armour and Hand Item from Inventory and Offhand
        equipmentInfo = new ArrayList<>(
                Arrays.asList(
                        new EquipmentInfoStack(player.getInventory().getArmorStack(3)),
                        new EquipmentInfoStack(player.getInventory().getArmorStack(2)),
                        new EquipmentInfoStack(player.getInventory().getArmorStack(1)),
                        new EquipmentInfoStack(player.getInventory().getArmorStack(0)),
                        new EquipmentInfoStack(player.getOffHandStack()),
                        new EquipmentInfoStack(player.getMainHandStack())
                )
        );

        // Remove Air Blocks from the list
        equipmentInfo.removeIf(equipment -> equipment.getItem().getItem().equals(Blocks.AIR.asItem()));

        // Check showNonTools
        if (!config.equipmentStatus.showNonTools) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().getItem().getMaxDamage() == 0);
        }

        if (config.equipmentStatus.Durability.showDurability) {
            getDurability(player);
        }

        // Check Config for Item Slot Disabled
        if (!config.equipmentStatus.slots.Head) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().equals(player.getInventory().getArmorStack(3)));
        }
        if (!config.equipmentStatus.slots.Body) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().equals(player.getInventory().getArmorStack(2)));
        }
        if (!config.equipmentStatus.slots.Legs) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().equals(player.getInventory().getArmorStack(1)));
        }
        if (!config.equipmentStatus.slots.Boots) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().equals(player.getInventory().getArmorStack(0)));
        }
        if (!config.equipmentStatus.slots.OffHand) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().equals(player.getOffHandStack()));
        }
        if (!config.equipmentStatus.slots.MainHand) {
            equipmentInfo.removeIf(equipment -> equipment.getItem().equals(player.getMainHandStack()));
        }
    }

    private void getDurability(ClientPlayerEntity player) {
        // Get each Items String and Color
        for (EquipmentInfoStack index : equipmentInfo) {
            ItemStack item = index.getItem();

            // Check Config for Item Durability Disabled
            if (isItemDurabilityDisabled(player, item)) {
                index.setText("");
                continue;
            }

            // Check if item has durability (Tools, Weapons, Armor) or not (Blocks, Food, etc.)
            if (item.getMaxDamage() != 0) {
                int currentDurability = item.getMaxDamage() - item.getDamage();

                // Draw Durability
                if (!config.equipmentStatus.Durability.showDurabilityAsPercentage)  {
                    index.setText(String.format("%s/%s", currentDurability, item.getMaxDamage()));
                } else {
                    index.setText(String.format("%s%%", (currentDurability * 100) / item.getMaxDamage()));
                }

                if (config.equipmentStatus.Durability.showColour) {
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
                    if (player.getInventory().count(item.getItem()) == item.getCount()) {
                        index.setText(String.valueOf(item.getCount()));
                    } else {
                        index.setText((item.getCount() + " (" + player.getInventory().count(item.getItem()) + ")"));
                    }
                } else {
                    index.setText("");
                }

            }
        }
    }

    private boolean isItemDurabilityDisabled(ClientPlayerEntity player, ItemStack item) {
        if (item.equals(player.getMainHandStack())) {
            return !config.equipmentStatus.Durability.slots.MainHand;
        } else if (item.equals(player.getOffHandStack())) {
            return !config.equipmentStatus.Durability.slots.OffHand;
        } else if (item.equals(player.getInventory().getArmorStack(0))) {
            return !config.equipmentStatus.Durability.slots.Boots;
        } else if (item.equals(player.getInventory().getArmorStack(1))) {
            return !config.equipmentStatus.Durability.slots.Legs;
        } else if (item.equals(player.getInventory().getArmorStack(2))) {
            return !config.equipmentStatus.Durability.slots.Body;
        } else if (item.equals(player.getInventory().getArmorStack(3))) {
            return !config.equipmentStatus.Durability.slots.Head;
        } else {
            return false;
        }
    }

    private boolean isCacheValid = false;

    private boolean isCacheValid() {
        return isCacheValid;
    }

    public void setCacheValid(boolean cacheValid) {
        isCacheValid = cacheValid;
    }
}
