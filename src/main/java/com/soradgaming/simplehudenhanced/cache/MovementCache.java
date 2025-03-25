package com.soradgaming.simplehudenhanced.cache;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class MovementCache {
    private static MovementCache instance; // Singleton instance
    private float currentHeightOffset;
    // Deadlock prevention
    private boolean currentHeightOffsetDeadlock;
    private float currentHeightOffsetOLD;

    private MovementCache() {
    }

    public static MovementCache getInstance() {
        if(instance == null) {
            instance = new MovementCache();
        }
        return instance;
    }

    public void updateCache(ClientPlayerEntity player) {
        calculateCurrentHeightOffset(player);
    }

    public synchronized float getCurrentHeightOffset() {
        if (currentHeightOffsetDeadlock) {
            return currentHeightOffsetOLD;
        } else {
            return currentHeightOffset;
        }
    }

    private void calculateCurrentHeightOffset(LivingEntity player) {
        currentHeightOffsetOLD = currentHeightOffset;
        currentHeightOffsetDeadlock = true;

        // Crouching check after Elytra since you can do both at the same time
        float height = player.getEyeHeight(EntityPose.STANDING);
        if (player.isGliding()) {
            float ticksElytraFlying = (float) (player.fallDistance + 1.0);
            float flyingAnimation = MathHelper.clamp(ticksElytraFlying * 0.09F, 0.0F, 1.0F);
            float flyingHeight = player.getEyeHeight(EntityPose.GLIDING) / height;
            currentHeightOffset = MathHelper.lerp(flyingAnimation, 1.0F, flyingHeight);
        } else if (player.isSwimming()) {
            float swimmingAnimation = player.isInSwimmingPose() ? 1.0F : player.handSwingProgress;
            float swimmingHeight = player.getEyeHeight(EntityPose.SWIMMING) / height;
            currentHeightOffset = MathHelper.lerp(swimmingAnimation, 1.0F, swimmingHeight);
        } else if (player.isUsingRiptide()) {
            currentHeightOffset = player.getEyeHeight(EntityPose.SPIN_ATTACK) / height;
        } else if (player.isSneaking()) {
            currentHeightOffset = player.getEyeHeight(EntityPose.CROUCHING) / height;
        } else if (player.isSleeping()) {
            currentHeightOffset = player.getEyeHeight(EntityPose.SLEEPING) / height;
        } else if (player.deathTime > 0) {
            float dyingAnimation = ((float) player.deathTime + (float) 1.0 - 1.0F) / 20.0F * 1.6F;
            dyingAnimation = Math.min(1.0F, MathHelper.sqrt(dyingAnimation));
            float dyingHeight = player.getEyeHeight(EntityPose.DYING) / height;
            currentHeightOffset = MathHelper.lerp(dyingAnimation, 1.0F, dyingHeight);
        } else {
            currentHeightOffset = 1.0F;
        }

        currentHeightOffsetDeadlock = false;
    }
}
