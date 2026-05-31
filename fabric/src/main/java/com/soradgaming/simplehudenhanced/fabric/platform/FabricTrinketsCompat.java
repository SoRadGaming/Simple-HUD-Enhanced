package com.soradgaming.simplehudenhanced.fabric.platform;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.hud.EquipmentInfoStack;
import com.soradgaming.simplehudenhanced.utli.VanillaEquipmentHelper;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

final class FabricTrinketsCompat {
    private FabricTrinketsCompat() {
    }

    static List<EquipmentInfoStack> getEquipmentInfo(Player player, SimpleHudEnhancedConfig config) {
        Optional<TrinketComponent> trinketComponentOptional = TrinketsApi.getTrinketComponent(player);
        if (trinketComponentOptional.isEmpty()) {
            return VanillaEquipmentHelper.getEquipmentInfo(player);
        }

        List<Tuple<SlotReference, ItemStack>> trinketData = trinketComponentOptional.get().getAllEquipped();
        List<EquipmentInfoStack> equipmentInfo = new ArrayList<>();

        if (config.equipmentStatus.slots.Head) {
            addTrinketData(player, equipmentInfo, trinketData, "head");
        }
        if (config.equipmentStatus.slots.Body) {
            addTrinketData(player, equipmentInfo, trinketData, "chest");
        }
        if (config.equipmentStatus.slots.Legs) {
            addTrinketData(player, equipmentInfo, trinketData, "legs");
        }
        if (config.equipmentStatus.slots.Boots) {
            addTrinketData(player, equipmentInfo, trinketData, "feet");
        }
        if (config.equipmentStatus.slots.MainHand) {
            addTrinketData(player, equipmentInfo, trinketData, "hand");
        }
        if (config.equipmentStatus.slots.OffHand) {
            addTrinketData(player, equipmentInfo, trinketData, "offhand");
        }

        return equipmentInfo;
    }

    private static void addTrinketData(Player player, List<EquipmentInfoStack> equipmentInfo, List<Tuple<SlotReference, ItemStack>> trinketData, String selectedGroup) {
        ItemStack defaultItem = getDefaultItemForSlot(player, selectedGroup);
        if (defaultItem != null) {
            equipmentInfo.add(new EquipmentInfoStack(defaultItem));
        }

        for (Tuple<SlotReference, ItemStack> trinketPair : trinketData) {
            String group = trinketPair.getA().inventory().getSlotType().getGroup();
            if (group.equals(selectedGroup)) {
                equipmentInfo.add(new EquipmentInfoStack(trinketPair.getB()));
            }
        }
    }

    private static ItemStack getDefaultItemForSlot(Player player, String group) {
        return switch (group) {
            case "head" -> player.getInventory().getArmor(3);
            case "chest" -> player.getInventory().getArmor(2);
            case "legs" -> player.getInventory().getArmor(1);
            case "feet" -> player.getInventory().getArmor(0);
            case "hand" -> player.getMainHandItem();
            case "offhand" -> player.getOffhandItem();
            default -> null;
        };
    }
}
