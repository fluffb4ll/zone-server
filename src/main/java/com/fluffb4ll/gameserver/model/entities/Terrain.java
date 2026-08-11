package com.fluffb4ll.gameserver.model.entities;

import com.fluffb4ll.gameserver.util.Vector2D;
import com.fluffb4ll.gameserver.util.AtomicFloat;

public abstract class Terrain extends BaseEntity {
    private final AtomicFloat radius;

    public Terrain(Vector2D position, float radius) {
        super(position);

        this.radius = new AtomicFloat(radius);
    }

    public float getRadius() {
        return radius.floatValue();
    }
}
