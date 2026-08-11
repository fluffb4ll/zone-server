package com.fluffb4ll.gameserver.util;

import java.util.Random;

public class RandTools {
    private static final Random RAND = new Random();

    public static Vector2D generateRandomPoint(float radius, Vector2D pos) {
        double angle = RAND.nextDouble() * Math.PI * 2;
        double r = Math.sqrt(RAND.nextDouble()) * radius;

        float randomX = (float) (pos.x + r * Math.cos(angle));
        float randomY = (float) (pos.y + r * Math.sin(angle));

        return new Vector2D(randomX, randomY);
    }
}
