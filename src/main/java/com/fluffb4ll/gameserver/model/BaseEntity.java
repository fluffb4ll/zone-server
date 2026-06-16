package com.fluffb4ll.gameserver.model;

import java.util.UUID;

/**
 * Defines a basic entity
 */
public class BaseEntity {
    private final UUID uuid;
    private volatile Vector2D position;

    public BaseEntity(UUID uuid) {
        this.uuid = uuid;
    }

    public Vector2D getPosition() {
        return position.copy();
    }

    public UUID getUuid() {
        return uuid;
    }

    protected void setPosition(Vector2D position) {
        this.position = position.copy();
    }
}
