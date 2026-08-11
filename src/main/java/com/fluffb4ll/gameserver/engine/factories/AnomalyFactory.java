package com.fluffb4ll.gameserver.engine.factories;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.model.WorldManager;
import com.fluffb4ll.gameserver.model.entities.Anomaly;
import com.fluffb4ll.gameserver.model.enums.AnomalyType;
import com.fluffb4ll.gameserver.util.Vector2D;
import org.springframework.stereotype.Component;

@Component
public class AnomalyFactory extends EntityFactory {
    public AnomalyFactory(WorldManager worldManager, EventBus eventBus) {
        super(worldManager, eventBus);
    }

    public Anomaly create(AnomalyType type, Vector2D pos) {
        Anomaly anomaly = new Anomaly(
                pos,
                type.getDisplayName(),
                type.getMaxHealth(),
                type.getBaseDamage(),
                type.getSpeed(),
                this.eventBus,
                type.getElementalType(),
                type.getRadius(),
                type.getCooldownTime(),
                type.getChargeTime()
        );

        worldManager.spawnEntity(anomaly);
        return anomaly;
    }
}
