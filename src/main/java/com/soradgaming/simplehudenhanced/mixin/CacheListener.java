package com.soradgaming.simplehudenhanced.mixin;

import com.soradgaming.simplehudenhanced.cache.Cache;
import com.soradgaming.simplehudenhanced.cache.UpdateCacheEvent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class CacheListener {
    @Mixin(PlayerInventory.class)
    public static class PlayerInventoryMixins {
        // Called on all inventory updates
        @Inject(method = "markDirty", at = @At("HEAD"), cancellable = true)
        private void onSlotsUpdate(CallbackInfo ci) {
            ActionResult result = UpdateCacheEvent.EVENT.invoker().updateCache(Cache.EQUIPMENT);

            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }

        // Called on HotBar slot change
        @Inject(method = "getHotbarSize", at = @At("HEAD"))
        private static void onTestNumberHotbar(CallbackInfoReturnable<Integer> cir) {
            // Call event to update the equipment cache
            ActionResult result = UpdateCacheEvent.EVENT.invoker().updateCache(Cache.EQUIPMENT);

            if (result == ActionResult.FAIL) {
                cir.cancel();
            }
        }
    }

    @Mixin(PlayerEntity.class)
    public static class PlayerEntityMixins {
        // Called on Item drop
        @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"))
        private void onDropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
            // Call event to update the equipment cache
            ActionResult result = UpdateCacheEvent.EVENT.invoker().updateCache(Cache.EQUIPMENT);

            if (result == ActionResult.FAIL) {
                cir.cancel();
            }
        }
    }
}
