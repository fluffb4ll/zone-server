package com.fluffb4ll.gameserver.model.terrains;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.engine.factories.AnomalyFactory;
import com.fluffb4ll.gameserver.model.entities.Anomaly;
import com.fluffb4ll.gameserver.model.enums.AnomalyType;
import com.fluffb4ll.gameserver.util.RandTools;
import com.fluffb4ll.gameserver.util.Vector2D;

public class AnomalyTerrain extends SpawnerTerrain {
    private final AnomalyType spawningType;
    private final AnomalyFactory factory;

    public AnomalyTerrain(String id,
                          String displayName,
                          Vector2D position,
                          float radius,
                          int entityLimit,
                          EventBus eventBus,
                          AnomalyType spawningType,
                          AnomalyFactory factory) {
        float spawnCooldown = 0f;
        super(id, displayName, position, radius, spawnCooldown, entityLimit, eventBus);

        this.spawningType = spawningType;
        this.factory = factory;
    }

    public AnomalyType getSpawningType() {
        return spawningType;
    }

    /**
     * Метод, спавнящий аномалии в случайных точках внутри террейна.
     * В будущем террейны с аномалиями будут неизменяемой частью карты.
     * Ну или хотя бы иметь какую-то заранее заданную их часть, которая будет перманентной :р
     */
    @Override
    protected void spawn() {
        Anomaly anomaly = factory.spawn(spawningType, RandTools.generateRandomPoint(getRadius(), getPosition()));
        addEntity(anomaly);
        setTimer(getSpawnCooldown());
    }
}
