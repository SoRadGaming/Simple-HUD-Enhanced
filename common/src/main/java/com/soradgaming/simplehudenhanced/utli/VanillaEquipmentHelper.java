package com.soradgaming.simplehudenhanced.utli;

import com.soradgaming.simplehudenhanced.hud.EquipmentInfoStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.world.entity.player.Player;

public final class VanillaEquipmentHelper {
    private VanillaEquipmentHelper() {
    }

    public static List<EquipmentInfoStack> getEquipmentInfo(Player player) {
        return new ArrayList<>(
                Arrays.asList(
                        new EquipmentInfoStack(player.getInventory().getArmor(3)),
                        new EquipmentInfoStack(player.getInventory().getArmor(2)),
                        new EquipmentInfoStack(player.getInventory().getArmor(1)),
                        new EquipmentInfoStack(player.getInventory().getArmor(0)),
                        new EquipmentInfoStack(player.getOffhandItem()),
                        new EquipmentInfoStack(player.getMainHandItem())
                )
        );
    }
}
