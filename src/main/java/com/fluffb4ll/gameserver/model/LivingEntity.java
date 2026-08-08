package com.fluffb4ll.gameserver.model;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.engine.MovementValidator;
import com.fluffb4ll.gameserver.model.records.EntityDeathEvent;
import com.fluffb4ll.gameserver.util.AtomicFloat;
import com.fluffb4ll.gameserver.util.WorldLogger;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Defines a living entity, which can move and inflict and receive damage.
 */
public abstract class LivingEntity extends BaseEntity {
    private final AtomicInteger maxHealth;
    private final AtomicInteger health;
    private final AtomicInteger damage;
    private final AtomicFloat speed;
    private final EventBus eventBus;

    public LivingEntity(Vector2D position, int maxHealth, int damage, float speed, EventBus eventBus) {
        super(position);

        health = new AtomicInteger(maxHealth);
        this.maxHealth = new AtomicInteger(maxHealth);
        this.damage = new AtomicInteger(damage);
        this.speed = new AtomicFloat(speed);

        this.eventBus = eventBus;
    }

    public int getDamage() {
        return damage.get();
    }

    public int getHealth() {
        return health.get();
    }

    public float getSpeed() {
        return speed.get();
    }

    public int getMaxHealth() {
        return maxHealth.get();
    }

    public boolean isAlive() {
        return health.get() > 0;
    }

    protected void setHealth(int health) {
        this.health.set(health);
    }

    protected void setDamage(int damage) {
        this.damage.set(damage);
    }

    protected void setSpeed(float speed) {
        this.speed.set(speed);
    }

    protected void setMaxHealth(int maxHealth) {
        this.maxHealth.set(maxHealth);
    }

    // TODO: переписать движение
    public synchronized boolean move(Vector2D newPos) {
        if (!MovementValidator.isValidMove(getPosition(), newPos))
            return false;
        setPosition(newPos);
        WorldLogger.logEntityMove(getUuid().toString(), newPos.x, newPos.y);
        return true;
    }

    public synchronized void takeDamage(int damage) {
        int hp = health.addAndGet(-damage);
        if (hp > 0)
            return;
        this.health.set(0);
        eventBus.publish(new EntityDeathEvent(getUuid()));
    }

    public synchronized void heal(int healAmount) {
        int hp = health.addAndGet(healAmount);
        int maxHealth = this.maxHealth.get();
        if (hp > maxHealth)
            health.set(maxHealth);
    }
}
