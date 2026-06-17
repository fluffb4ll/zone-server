package com.fluffb4ll.gameserver.model;

import com.fluffb4ll.gameserver.model.enums.AnomalyType;
import com.fluffb4ll.gameserver.util.AtomicFloat;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Anomaly extends LivingEntity {
    private final AnomalyType type;
    private final AtomicFloat radius;
    private final boolean isStatic;

    public Anomaly(UUID uuid,
                   AtomicInteger maxHealth,
                   AtomicInteger damage,
                   EventBus eventBus,
                   AnomalyType type,
                   AtomicFloat radius,
                   boolean isStatic) {
        super(uuid, maxHealth, damage, eventBus);

        this.type = type;
        this.radius = new AtomicFloat(radius.get());
        this.isStatic = isStatic;
    }


    public AnomalyType getType() {
        return type;
    }

    public AtomicFloat getRadius() {
        return radius;
    }

    public boolean isStatic() {
        return isStatic;
    }
}
