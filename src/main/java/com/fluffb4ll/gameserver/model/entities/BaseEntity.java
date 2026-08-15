package com.fluffb4ll.gameserver.model.entities;

import com.fluffb4ll.gameserver.util.Vector2D;

import java.util.Objects;
import java.util.UUID;

/**
 * Defines a basic entity
 */
public abstract class BaseEntity {
    private final String id;
    private Vector2D position;

    public BaseEntity(String id, Vector2D position) {
        this.id = id;
        this.position = position;
    }

    public Vector2D getPosition() {
        return position.copy();
    }

    public String getId() {
        return id;
    }

    protected synchronized void setPosition(Vector2D position) {
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
