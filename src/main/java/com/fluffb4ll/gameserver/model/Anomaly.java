package com.fluffb4ll.gameserver.model;

import com.fluffb4ll.gameserver.model.enums.AnomalyState;
import com.fluffb4ll.gameserver.model.enums.AnomalyType;
import com.fluffb4ll.gameserver.util.AtomicFloat;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Anomaly extends LivingEntity {
    private final AnomalyType type;
    private final AtomicFloat radius;
    private final boolean isStatic;

    private AnomalyState state;

    private final Set<LivingEntity> targetsInRange = ConcurrentHashMap.newKeySet();

    private float timer = 0f;
    // TODO: зависимость переменных от типа аномалии, парсинг из конфига
    private static final float COOLDOWN_TIME = 3f;
    private static final float CHARGE_TIME = 0.5f;

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

    public boolean addTarget(LivingEntity target) {
        return targetsInRange.add(target);
    }

    public boolean removeTarget(LivingEntity target) {
        return targetsInRange.remove(target);
    }

    public void update(float deltaTime) {
        targetsInRange.removeIf(target -> !target.isAlive());

        switch (state) {
            case IDLE -> {
                if (!targetsInRange.isEmpty()) {
                    state = AnomalyState.TRIGGERED;
                    timer = CHARGE_TIME;
                }
            }
            case TRIGGERED -> {
                timer -= deltaTime;
                if (timer <= 0f)
                    burst();
            }
            case COOLDOWN -> {
                timer -= deltaTime;
                if (timer <= 0f)
                    state = AnomalyState.IDLE;
            }
        }
    }

    public void burst() {
        for (LivingEntity target : targetsInRange)
            target.takeDamage(getDamage());

        state = AnomalyState.COOLDOWN;
        timer = COOLDOWN_TIME;
    }
}
