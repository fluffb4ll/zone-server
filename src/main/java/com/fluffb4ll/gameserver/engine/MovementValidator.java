package com.fluffb4ll.gameserver.engine;

import com.fluffb4ll.gameserver.model.Vector2D;

public class MovementValidator {

    /**
     * Checks the given move's validity to determine whether an entity is trying to cheat.
     * For now, it always returns true.
     * @param oldPos The current position of an entity.
     * @param newPos The desired new position of an entity.
     * @return true, if a move is valid; false otherwise.
     */
    public static boolean isValidMove(Vector2D oldPos, Vector2D newPos) {
        return true;
    }
}
