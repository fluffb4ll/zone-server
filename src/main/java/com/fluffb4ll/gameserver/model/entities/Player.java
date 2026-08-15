package com.fluffb4ll.gameserver.model.entities;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.util.Vector2D;

import java.util.UUID;

/**
 * Defines a player entity.
 */
public class Player extends LivingEntity {
    public Player(UUID id,
                  Vector2D position,
                  String displayName,
                  int maxHealth,
                  int damage,
                  float speed,
                  EventBus eventBus) {
        super(id, position, displayName, maxHealth, damage, speed, eventBus);
    }
}
