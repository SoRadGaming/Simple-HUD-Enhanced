package com.soradgaming.simplehudenhanced.utli;

import com.google.common.collect.Maps;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.Map;

public class StatusEffectsTracker {
    // Instance
    private static StatusEffectsTracker instance;

    // Map to store the active status effects
    private final Map<StatusEffect, Integer> activeStatusEffectsMax = Maps.newHashMap();

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
    public int getMaxDuration(StatusEffectInstance effect) {
        // This Function is always called with the effect the player has only (we need to manage adding removing and updated if new value is higher than the current one)
        if (activeStatusEffectsMax.get(effect.getEffectType()) == null) {
            setMaxDuration(effect, effect.getDuration());
            return effect.getDuration();
        }

        if (effect.getDuration() > activeStatusEffectsMax.get(effect.getEffectType())) {
            setMaxDuration(effect, effect.getDuration());
        }

        return activeStatusEffectsMax.get(effect.getEffectType());
    }

    // Set the max duration of a status effect
    public void setMaxDuration(StatusEffectInstance effect, int duration) {
        activeStatusEffectsMax.put(effect.getEffectType(), duration);
    }

    public void removeStatusEffect(StatusEffect effect) {
        // Called on all effects that are removed, we need to filter out the ones that are not in the map
        if (activeStatusEffectsMax.get(effect) != null) {
            activeStatusEffectsMax.remove(effect);
        }
    }
}
