package com.fluffb4ll.gameserver.engine.entities;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.util.Vector2D;
import com.fluffb4ll.gameserver.model.enums.AnomalyState;
import com.fluffb4ll.gameserver.model.enums.AnomalyElementalType;
import com.fluffb4ll.gameserver.util.AtomicFloat;
import com.fluffb4ll.gameserver.util.WorldLogger;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Anomaly extends LivingEntity {
    private final AnomalyElementalType type;
    private final AtomicFloat radius;
    private final boolean isStatic;

    private AnomalyState state;

    private final Set<LivingEntity> targetsInRange = ConcurrentHashMap.newKeySet();

    private float timer = 0f;

    private final float cooldownTime;
    private final float chargeTime;

    public Anomaly(UUID id,
                   Vector2D position,
                   String displayName,
                   int maxHealth,
                   int damage,
                   float speed,
                   EventBus eventBus,
                   AnomalyElementalType type,
                   float radius,
                   float cooldownTime,
                   float chargeTime) {
        super(id, position, displayName, maxHealth, damage, speed, eventBus);

        this.type = type;
        this.radius = new AtomicFloat(radius);
        this.cooldownTime = cooldownTime;
        this.chargeTime = chargeTime;

        isStatic = speed <= 0f;
        this.state = AnomalyState.IDLE;
    }


    public AnomalyElementalType getType() {
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

    // TODO: прописать движение нестатичных аномалий?
    public void update(float deltaTime) {
        targetsInRange.removeIf(target -> !target.isAlive());

        switch (state) {
            case IDLE -> {
                if (!targetsInRange.isEmpty()) {
                    state = AnomalyState.TRIGGERED;
                    if (chargeTime > 0f)
                        timer = chargeTime;
                    else
                        burst();
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

    private void burst() {
        for (LivingEntity target : targetsInRange) {
            target.takeDamage(getDamage());
            WorldLogger.logAnomalyHit(
                    target.getDisplayName(), target.getUuid(), target.getHealth(),
                    getDisplayName(), getUuid()
            );
        }

        if (cooldownTime > 0f) {
            state = AnomalyState.COOLDOWN;
            timer = cooldownTime;
        }
        else
            state = AnomalyState.IDLE;
    }

    public boolean isColliding(LivingEntity entity) {
        float dx = entity.getPosition().x - getPosition().x;
        float dy = entity.getPosition().y - getPosition().y;
        float distSquared = dx * dx + dy * dy;

        float radius = getRadius();
        return distSquared <= radius * radius;
    }
}
