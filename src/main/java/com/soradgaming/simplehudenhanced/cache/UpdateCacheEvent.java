package com.soradgaming.simplehudenhanced.cache;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface UpdateCacheEvent {
    Event<UpdateCacheEvent> EVENT = EventFactory.createArrayBacked(UpdateCacheEvent.class,
            (listeners) -> (cache) -> {
                for (UpdateCacheEvent listener : listeners) {
                    ActionResult result = listener.updateCache(cache);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult updateCache(Cache cache);
}
