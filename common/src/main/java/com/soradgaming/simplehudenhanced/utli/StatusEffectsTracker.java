package com.soradgaming.simplehudenhanced.utli;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class StatusEffectsTracker {
    // Instance
    private static StatusEffectsTracker instance;

    // Map to store the active status effects
    private final Map<Holder<MobEffect>, Integer> activeStatusEffectsMax = Maps.newHashMap();

    // Constructor
    private StatusEffectsTracker() {}

    // Initialization method
    public static void initialize() {
        if (instance == null) {
            instance = new StatusEffectsTracker();
        }
    }

    // Singleton instance getter
    public static StatusEffectsTracker getInstance() {
        if (instance == null) {
            initialize();
        }
        return instance;
    }

    // Method to get the max duration of a status effect
    public int getMaxDuration(MobEffectInstance effect) {
        // This Function is always called with the effect the player has only (we need to manage adding removing and updated if new value is higher than the current one)
        if (activeStatusEffectsMax.get(effect.getEffect()) == null) {
            setMaxDuration(effect, effect.getDuration());
            return effect.getDuration();
        }

        if (effect.getDuration() > activeStatusEffectsMax.get(effect.getEffect())) {
            setMaxDuration(effect, effect.getDuration());
        }

        return activeStatusEffectsMax.get(effect.getEffect());
    }

    // Set the max duration of a status effect
    public void setMaxDuration(MobEffectInstance effect, int duration) {
        activeStatusEffectsMax.put(effect.getEffect(), duration);
    }

    public void removeStatusEffect(Holder<MobEffect> effect) {
        // Called on all effects that are removed, we need to filter out the ones that are not in the map
        if (activeStatusEffectsMax.get(effect) != null) {
            activeStatusEffectsMax.remove(effect);
        }
    }
}
