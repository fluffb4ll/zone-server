package com.fluffb4ll.gameserver.engine.factories;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.model.WorldManager;

public abstract class EntityFactory {
    protected final WorldManager worldManager;
    protected final EventBus eventBus;

    public EntityFactory(WorldManager worldManager, EventBus eventBus) {
        this.worldManager = worldManager;
        this.eventBus = eventBus;
    }
}
