package com.fluffb4ll.gameserver.engine.entities;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.engine.terrains.MutantNest;
import com.fluffb4ll.gameserver.util.RandTools;
import com.fluffb4ll.gameserver.util.Vector2D;
import com.fluffb4ll.gameserver.model.enums.MutantBehaviour;
import com.fluffb4ll.gameserver.model.enums.MutantState;

import java.util.Random;
import java.util.UUID;

public class Mutant extends LivingEntity {
    private static final Random RAND = new Random();

    private MutantBehaviour behaviour;

    private MutantState state = MutantState.IDLE;

    private final MutantNest home;
    private Vector2D targetPosition;

    private final float wanderRadius;

    private float timer = 0f;

    public Mutant(UUID id,
                  Vector2D position,
                  String displayName,
                  int maxHealth,
                  int damage,
                  float speed,
                  EventBus eventBus,
                  MutantBehaviour behaviour,
                  MutantNest home,
                  float wanderRadius) {
        super(id, position, displayName, maxHealth, damage, speed, eventBus);

        this.behaviour = behaviour;
        this.home = home;
        this.wanderRadius = wanderRadius;
        timer = 1f + RAND.nextFloat() * 2f;

        setPosition(position);
    }

    public MutantBehaviour getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(MutantBehaviour behaviour) {
        this.behaviour = behaviour;
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
            targetPosition = RandTools.generateRandomPoint(wanderRadius, home.getPosition());

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
}
