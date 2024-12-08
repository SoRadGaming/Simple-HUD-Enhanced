package com.soradgaming.simplehudenhanced.utli;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.hud.EquipmentInfoStack;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.soradgaming.simplehudenhanced.SimpleHudEnhanced.isTrinketsInstalled;

public class TrinketAccessor {
    private final PlayerEntity player;
    private List<EquipmentInfoStack> equipmentInfo;
    private final SimpleHudEnhancedConfig config;

    public TrinketAccessor(PlayerEntity player, SimpleHudEnhancedConfig config) {
        this.player = player;
        this.config = config;
        setEquipmentInfo();
    }

    public List<EquipmentInfoStack> getEquipmentInfo() {
        return equipmentInfo;
    }

    public void setEquipmentInfo() {
        // Check if Trinket mod is installed
        if (isTrinketsInstalled()) {
            Optional<TrinketComponent> trinketComponentOptional = TrinketsApi.getTrinketComponent(this.player);

            if (trinketComponentOptional.isPresent()) {
                TrinketComponent trinketComponent = trinketComponentOptional.get();
                // Initialize trinketData
                List<Pair<SlotReference, ItemStack>> trinketData = trinketComponent.getAllEquipped();
                equipmentInfo = new ArrayList<>();

                if (config.equipmentStatus.slots.Head) {
                    addTrinketData(trinketData, "head");
                }
                if (config.equipmentStatus.slots.Body) {
                    addTrinketData(trinketData, "chest");
                }
                if (config.equipmentStatus.slots.Legs) {
                    addTrinketData(trinketData, "legs");
                }
                if (config.equipmentStatus.slots.Boots) {
                    addTrinketData(trinketData, "feet");
                }
                if (config.equipmentStatus.slots.MainHand) {
                    addTrinketData(trinketData, "hand");
                }
                if (config.equipmentStatus.slots.OffHand) {
                    addTrinketData(trinketData, "offhand");
                }
            }
        } else {
            // Trinket is not installed, handle accordingly
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
        }
    }

    private void addTrinketData(List<Pair<SlotReference, ItemStack>> trinketData, String selectedGroup) {
        if (trinketData.isEmpty()) {
            ItemStack defaultItem = getDefaultItemForSlot(selectedGroup);
            if (defaultItem != null) {
                equipmentInfo.add(new EquipmentInfoStack(defaultItem));
            }
            return;
        }

        int x = 0;
        for (Pair<SlotReference, ItemStack> trinketPair : trinketData) {
            String group = trinketPair.getLeft().inventory().getSlotType().getGroup();
            // Run once
            if (x == 0) {
                ItemStack defaultItem = getDefaultItemForSlot(selectedGroup);
                if (defaultItem != null) {
                    equipmentInfo.add(new EquipmentInfoStack(defaultItem));
                }
                x++;
            }

            if (group.equals(selectedGroup)) {
                equipmentInfo.add(new EquipmentInfoStack(trinketPair.getRight()));
            }
        }
    }

    // Assuming you have a method to get default items based on the slot
    private ItemStack getDefaultItemForSlot(String group) {
        return switch (group) {
            case "head" -> this.player.getInventory().getArmorStack(3);
            case "chest" -> this.player.getInventory().getArmorStack(2);
            case "legs" -> this.player.getInventory().getArmorStack(1);
            case "feet" -> this.player.getInventory().getArmorStack(0);
            case "hand" -> this.player.getMainHandStack();
            case "offhand" -> this.player.getOffHandStack();
            default -> null;
        };
    }
}
