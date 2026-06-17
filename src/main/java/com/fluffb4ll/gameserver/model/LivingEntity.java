package com.fluffb4ll.gameserver.model;

import com.fluffb4ll.gameserver.engine.MovementValidator;
import com.fluffb4ll.gameserver.model.records.EntityDeathEvent;
import com.fluffb4ll.gameserver.util.AtomicFloat;

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

    public LivingEntity(UUID uuid, AtomicInteger maxHealth, AtomicInteger damage, EventBus eventBus) {
        super(uuid);

        health = new AtomicInteger(maxHealth.get());
        this.maxHealth = new AtomicInteger(maxHealth.get());
        this.damage = new AtomicInteger(damage.get());
        speed = new AtomicFloat();

        this.eventBus = eventBus;
    }

    public int getDamage() {
        return damage.get();
    }

    public int getHealth() {
        return health.get();
    }

    public AtomicFloat getSpeed() {
        return speed;
    }

    public int getMaxHealth() {
        return maxHealth.get();
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
