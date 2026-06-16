package com.fluffb4ll.gameserver.model;

public class Vector2D {
    public float x;
    public float y;

    public Vector2D() {
        this(0f, 0f);
    }

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D copy() {
        return new Vector2D(x, y);
    }
}
