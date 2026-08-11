package com.fluffb4ll.gameserver.model.terrains;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.engine.factories.MutantFactory;
import com.fluffb4ll.gameserver.model.entities.Mutant;
import com.fluffb4ll.gameserver.model.enums.MutantType;
import com.fluffb4ll.gameserver.util.RandTools;
import com.fluffb4ll.gameserver.util.Vector2D;

public class MutantNest extends SpawnerTerrain {
    private final MutantType spawningType;

    private final MutantFactory factory;

    public MutantNest(Vector2D position, float radius, float spawningTimer, int entityLimit, EventBus eventBus,
                      MutantType spawningType, MutantFactory factory) {
        super(position, radius, spawningTimer, entityLimit, eventBus);

        this.spawningType = spawningType;
        this.factory = factory;
    }

    public MutantType getSpawningType() {
        return spawningType;
    }

    @Override
    protected void spawn() {
        Mutant mutant = factory.create(spawningType, RandTools.generateRandomPoint(getRadius(), getPosition()));
        addEntity(mutant);
        setTimer(getSpawnCooldown());
    }
}