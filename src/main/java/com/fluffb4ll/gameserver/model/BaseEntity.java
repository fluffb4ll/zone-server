package com.fluffb4ll.gameserver.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Defines a basic entity
 */
public abstract class BaseEntity {
    private final UUID id;
    private volatile Vector2D position;

    public BaseEntity(UUID id) {
        this.id = id;
    }

    public Vector2D getPosition() {
        return position.copy();
    }

    public UUID getUuid() {
        return id;
    }

    protected void setPosition(Vector2D position) {
        this.position = position.copy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
