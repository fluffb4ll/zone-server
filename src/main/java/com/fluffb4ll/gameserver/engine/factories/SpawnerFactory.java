package com.fluffb4ll.gameserver.engine.factories;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.model.WorldManager;
import com.fluffb4ll.gameserver.model.enums.MutantType;
import com.fluffb4ll.gameserver.model.terrains.MutantNest;
import com.fluffb4ll.gameserver.util.Vector2D;
import org.springframework.stereotype.Component;

@Component
public class SpawnerFactory extends TerrainFactory {
    private final MutantFactory mutantFactory;

    public SpawnerFactory(WorldManager worldManager, EventBus eventBus, MutantFactory mutantFactory) {
        super(worldManager, eventBus);

        this.mutantFactory = mutantFactory;
    }

    public MutantNest createNest(String displayName,
                                 Vector2D pos,
                                 float radius,
                                 MutantType spawningType,
                                 int entityLimit) {
        return new MutantNest(
                displayName,
                pos,
                radius,
                entityLimit,
                eventBus,
                spawningType,
                mutantFactory
        );
    }

    public MutantNest spawnNest(String displayName,
                                Vector2D pos,
                                float radius,
                                MutantType spawningType,
                                int entityLimit) {
        MutantNest nest = createNest(displayName, pos, radius, spawningType, entityLimit);
        worldManager.spawnTerrain(nest);
        return nest;
    }
}
