package com.fluffb4ll.gameserver.engine.factories;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.model.WorldManager;
import com.fluffb4ll.gameserver.model.enums.AnomalyType;
import com.fluffb4ll.gameserver.model.enums.MutantType;
import com.fluffb4ll.gameserver.model.terrains.AnomalyTerrain;
import com.fluffb4ll.gameserver.model.terrains.MutantNest;
import com.fluffb4ll.gameserver.util.IdGeneratorUtil;
import com.fluffb4ll.gameserver.util.Vector2D;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
        UUID id = IdGeneratorUtil.generateId();
        return new MutantNest(
                id,
                displayName,
                pos,
                radius,
                entityLimit,
                eventBus,
                spawningType,
                mutantFactory
        );
    }

    public MutantNest createNest(UUID id,
                                 String displayName,
                                 Vector2D pos,
                                 float radius,
                                 MutantType spawningType,
                                 int entityLimit) {
        return new MutantNest(
                id,
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

    public MutantNest spawnNest(UUID id,
                                String displayName,
                                Vector2D pos,
                                float radius,
                                MutantType spawningType,
                                int entityLimit) {
        MutantNest nest = createNest(id, displayName, pos, radius, spawningType, entityLimit);
        worldManager.spawnTerrain(nest);
        return nest;
    }

    public AnomalyTerrain createAnomalies(String displayName,
                                          Vector2D pos,
                                          float radius,
                                          AnomalyType spawningType,
                                          int entityLimit) {
        UUID id = IdGeneratorUtil.generateId();
        return new AnomalyTerrain(
                id,
                displayName,
                pos,
                radius,
                entityLimit,
                eventBus,
                spawningType,
                anomalyFactory
        );
    }

    public AnomalyTerrain createAnomalies(UUID id,
                                          String displayName,
                                          Vector2D pos,
                                          float radius,
                                          AnomalyType spawningType,
                                          int entityLimit) {
        return new AnomalyTerrain(
                id,
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

    public AnomalyTerrain spawnAnomalies(UUID id,
                                         String displayName,
                                         Vector2D pos,
                                         float radius,
                                         AnomalyType spawningType,
                                         int entityLimit) {
        AnomalyTerrain aTerrain = createAnomalies(id, displayName, pos, radius, spawningType, entityLimit);
        worldManager.spawnTerrain(aTerrain);
        return aTerrain;
    }
}
