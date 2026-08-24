package com.fluffb4ll.gameserver.model.records.events;

import com.fluffb4ll.gameserver.engine.entities.LivingEntity;
import com.fluffb4ll.gameserver.util.Vector2D;

public record EntityMoveEvent(LivingEntity entity, Vector2D oldPos, Vector2D newPos) {
}
