package com.soradgaming.simplehudenhanced.mixin;

import com.soradgaming.simplehudenhanced.utli.StatusEffectsTracker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "removeStatusEffectInternal(Lnet/minecraft/entity/effect/StatusEffect;)Lnet/minecraft/entity/effect/StatusEffectInstance;", at = @At("HEAD"))
    private void removeStatusEffectInternal(StatusEffect effect, CallbackInfoReturnable<Boolean> cir) {
        // Check Status Effect Tracker for removal
        StatusEffectsTracker.getInstance().removeStatusEffect(effect);
    }
}
