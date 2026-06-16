package com.fluffb4ll.gameserver.model;

import com.fluffb4ll.gameserver.engine.MovementValidator;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Defines a living entity, which can move and inflict and receive damage.
 */
public class LivingEntity extends BaseEntity {
    private volatile AtomicInteger maxHealth;
    private AtomicInteger health;
    private volatile AtomicInteger damage;
    private volatile float speed;

    public LivingEntity(UUID uuid, AtomicInteger maxHealth, AtomicInteger damage) {
        super(uuid);

        health = maxHealth;
        this.maxHealth = maxHealth;
        this.damage = damage;
    }

    public int getDamage() {
        return damage.get();
    }

    public int getHealth() {
        return health.get();
    }

    public float getSpeed() {
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
        this.speed = speed;
    }

    protected void setMaxHealth(int maxHealth) {
        this.maxHealth.set(maxHealth);
    }

    public boolean move(Vector2D newPos) {
        if (MovementValidator.isValidMove(getPosition(), newPos)) {
            setPosition(newPos);
            return true;
        }
        return false;
    }

    public void takeDamage(int damage) {
        int hp = health.addAndGet(-damage);
        if (hp > 0)
            return;
        this.health.set(0);
        onDeath();
    }

    public void heal(int healAmount) {
        int hp = health.addAndGet(healAmount);
        int maxHealth = this.maxHealth.get();
        if (hp > maxHealth)
            health.set(maxHealth);
    }

    private void onDeath() {

    }
}
