package com.fluffb4ll.gameserver.util;

import com.fluffb4ll.gameserver.engine.entities.LivingEntity;

public class MovementValidator {

    /**
     * Checks the given move's validity to determine whether an entity is trying to cheat.
     * For now, it always returns true.
     * @param oldPos The current position of an entity.
     * @param newPos The desired new position of an entity.
     * @return true, if a move is valid; false otherwise.
     */
    public static boolean isValidMove(LivingEntity entity, Vector2D oldPos, Vector2D newPos) {
        return entity.isAlive();
    }
}
