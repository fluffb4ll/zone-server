package com.fluffb4ll.gameserver.engine.factories;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.model.WorldManager;
import com.fluffb4ll.gameserver.model.enums.AnomalyType;
import com.fluffb4ll.gameserver.model.enums.MutantType;
import com.fluffb4ll.gameserver.model.terrains.AnomalyTerrain;
import com.fluffb4ll.gameserver.model.terrains.MutantNest;
import com.fluffb4ll.gameserver.util.Vector2D;
import org.springframework.stereotype.Component;

@Component
public class SpawnerFactory extends TerrainFactory {
    private final MutantFactory mutantFactory;
    private final AnomalyFactory anomalyFactory;

    public SpawnerFactory(WorldManager worldManager, EventBus eventBus, MutantFactory mutantFactory, AnomalyFactory anomalyFactory) {
        super(worldManager, eventBus);

        this.mutantFactory = mutantFactory;
        this.anomalyFactory = anomalyFactory;
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

    public AnomalyTerrain createAnomalies(String displayName,
                                          Vector2D pos,
                                          float radius,
                                          AnomalyType spawningType,
                                          int entityLimit) {
        return new AnomalyTerrain(
                displayName,
                pos,
                radius,
                entityLimit,
                eventBus,
                spawningType,
                anomalyFactory
        );
    }

    public AnomalyTerrain spawnAnomalies(String displayName,
                                         Vector2D pos,
                                         float radius,
                                         AnomalyType spawningType,
                                         int entityLimit) {
        AnomalyTerrain aTerrain = createAnomalies(displayName, pos, radius, spawningType, entityLimit);
        worldManager.spawnTerrain(aTerrain);
        return aTerrain;
    }
}
