package com.fluffb4ll.gameserver.engine.factories;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.model.WorldManager;
import com.fluffb4ll.gameserver.model.entities.Mutant;
import com.fluffb4ll.gameserver.model.enums.MutantType;
import com.fluffb4ll.gameserver.model.terrains.MutantNest;
import com.fluffb4ll.gameserver.util.Vector2D;
import org.springframework.stereotype.Component;

@Component
public class MutantFactory extends EntityFactory {
    public MutantFactory(WorldManager worldManager, EventBus eventBus) {
        super(worldManager, eventBus);
    }

    public Mutant create(MutantType type, MutantNest nest, Vector2D pos) {
        return new Mutant(
                pos,
                type.getDisplayName(),
                type.getMaxHealth(),
                type.getBaseDamage(),
                type.getSpeed(),
                this.eventBus,
                type.getBehaviour(),
                nest,
                type.getWanderRadius()
        );
    }

    public Mutant spawn(MutantType type, MutantNest nest, Vector2D pos) {
        Mutant mutant = create(type, nest, pos);
        worldManager.spawnEntity(mutant);
        return mutant;
    }
}
