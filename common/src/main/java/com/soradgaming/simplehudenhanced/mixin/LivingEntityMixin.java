package com.soradgaming.simplehudenhanced.mixin;

import com.soradgaming.simplehudenhanced.utli.StatusEffectsTracker;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "removeEffectNoUpdate", at = @At("HEAD"))
    private void removeStatusEffectInternal(Holder<MobEffect> effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        // Check Status Effect Tracker for removal
        StatusEffectsTracker.getInstance().removeStatusEffect(effect);
    }
}
