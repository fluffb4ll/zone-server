package com.fluffb4ll.gameserver.engine.terrains;

import com.fluffb4ll.gameserver.util.Vector2D;
import com.fluffb4ll.gameserver.util.AtomicFloat;

import java.util.Objects;
import java.util.UUID;

public abstract class Terrain {
    private final UUID id;
    private final String displayName;
    private Vector2D position;
    private final AtomicFloat radius;

    public Terrain(UUID id,
                   String displayName,
                   Vector2D position,
                   float radius) {
        this.id = id;
        this.displayName = displayName;
        this.position = position;
        this.radius = new AtomicFloat(radius);
    }

    public Vector2D getPosition() {
        return position.copy();
    }

    public UUID getUuid() {
        return id;
    }

    public float getRadius() {
        return radius.floatValue();
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

        Terrain that = (Terrain) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
