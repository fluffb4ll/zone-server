package com.fluffb4ll.gameserver.model;

import com.fluffb4ll.gameserver.model.enums.AnomalyState;
import com.fluffb4ll.gameserver.model.enums.AnomalyType;
import com.fluffb4ll.gameserver.util.AtomicFloat;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Anomaly extends LivingEntity {
    private final AnomalyType type;
    private final AtomicFloat radius;
    private final boolean isStatic;

    private AnomalyState state;

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

        this.state = AnomalyState.IDLE;
    }


    public AnomalyType getType() {
        return type;
    }

    public float getRadius() {
        return radius.floatValue();
    }

    public AnomalyState getState() {
        return state;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public synchronized void setState(AnomalyState state) {
        this.state = state;
    }
}
