package com.fluffb4ll.gameserver.model;

import com.fluffb4ll.gameserver.model.enums.MutantBehaviour;
import com.fluffb4ll.gameserver.model.enums.MutantState;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Mutant extends LivingEntity {
    private static final Random RAND = new Random();

    private final String species;
    private MutantBehaviour behaviour;

    private MutantState state;

    private final Vector2D homePosition;
    private Vector2D targetPosition;

    private float wanderRadius = 15f;

    private float timer = 0f;

    public Mutant(UUID uuid,
                  Vector2D position,
                  int maxHealth,
                  int damage,
                  EventBus eventBus,
                  String species,
                  MutantBehaviour behaviour) {
        super(uuid, position, maxHealth, damage, eventBus);

        this.species = species;
        this.behaviour = behaviour;

        this.homePosition = position;
        this.setPosition(generateRandomWanderPoint());
        timer = 1f + RAND.nextFloat() * 2f;
    }

    public MutantBehaviour getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(MutantBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    public String getSpecies() {
        return species;
    }

    public MutantState getState() {
        return state;
    }

    public synchronized void setState(MutantState state) {
        this.state = state;
    }

    public void updateAI(float deltaTime) {
        if (!isAlive())
            return;

        switch (state) {
            case IDLE -> handleIdleState(deltaTime);
            case WANDER -> handleWanderState(deltaTime);
        }
    }

    private void handleIdleState(float deltaTime) {
        timer -= deltaTime;
        if (timer <= 0f) {
            targetPosition = generateRandomWanderPoint();
            state = MutantState.WANDER;
        }
    }

    private void handleWanderState(float deltaTime) {
        if (targetPosition == null) {
            state = MutantState.IDLE;
            return;
        }

        if (hasReachedTarget()) {
            state = MutantState.IDLE;
            timer = 2f + RAND.nextFloat() * 3f;
            targetPosition = null;
        }
    }

    public Vector2D calculateNextPosition(float deltaTime) {
        if (state == MutantState.IDLE || targetPosition == null)
            return getPosition();

        float dx = targetPosition.x - getPosition().x;
        float dy = targetPosition.y - getPosition().y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        float moveDistance = getSpeed() * deltaTime;

        if (moveDistance >= distance)
            return targetPosition;

        float newX = getPosition().x + (dx / distance) * moveDistance;
        float newY = getPosition().y + (dy / distance) * moveDistance;

        return new Vector2D(newX, newY);
    }

    private boolean hasReachedTarget() {
        if (targetPosition == null)
            return true;
        float dx = targetPosition.x - getPosition().x;
        float dy = targetPosition.y - getPosition().y;
        return (dx * dx + dy * dy) <= 0.05f;
    }

    private Vector2D generateRandomWanderPoint() {
        double angle = RAND.nextDouble() * Math.PI * 2;
        double r = Math.sqrt(RAND.nextDouble()) * wanderRadius;

        float randomX = (float) (homePosition.x + r * Math.cos(angle));
        float randomY = (float) (homePosition.y + r * Math.sin(angle));

        return new Vector2D(randomX, randomY);
    }
}
