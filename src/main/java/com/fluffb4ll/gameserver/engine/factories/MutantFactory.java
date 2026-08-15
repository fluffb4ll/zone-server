package com.fluffb4ll.gameserver.engine.factories;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.model.WorldManager;
import com.fluffb4ll.gameserver.model.entities.Mutant;
import com.fluffb4ll.gameserver.model.enums.MutantType;
import com.fluffb4ll.gameserver.model.terrains.MutantNest;
import com.fluffb4ll.gameserver.util.IdGeneratorUtil;
import com.fluffb4ll.gameserver.util.Vector2D;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MutantFactory extends EntityFactory {
    public MutantFactory(WorldManager worldManager, EventBus eventBus) {
        super(worldManager, eventBus);
    }

    public Mutant create(MutantType type, MutantNest nest, Vector2D pos) {
        UUID id = IdGeneratorUtil.generateId();
        return new Mutant(
                id,
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

    public Mutant create(UUID id, MutantType type, MutantNest nest, Vector2D pos) {
        return new Mutant(
                id,
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

    public Mutant spawn(UUID id, MutantType type, MutantNest nest, Vector2D pos) {
        Mutant mutant = create(id, type, nest, pos);
        worldManager.spawnEntity(mutant);
        return mutant;
    }
}
