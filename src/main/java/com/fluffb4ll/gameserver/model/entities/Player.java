package com.fluffb4ll.gameserver.model.entities;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.util.Vector2D;

/**
 * Defines a player entity.
 */
public class Player extends LivingEntity {
    public Player(Vector2D position, String displayName, int maxHealth, int damage, float speed, EventBus eventBus) {
        super(position, displayName, maxHealth, damage, speed, eventBus);
    }
}
