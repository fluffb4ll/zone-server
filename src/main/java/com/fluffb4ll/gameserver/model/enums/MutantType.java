package com.fluffb4ll.gameserver.model.enums;

public enum MutantType {
    BLIND_DOG("Blind Dog", 100, 12.0f, 15,
            MutantBehaviour.COWARD, 10f),
    FLESH("Flesh", 150, 8.0f, 10,
            MutantBehaviour.NEUTRAL, 20f),
    BLOODSUCKER("Bloodsucker", 250, 18.0f,
            45, MutantBehaviour.AGGRESSIVE, 50f);

    private final String displayName;
    private final int maxHealth;
    private final float speed;
    private final int baseDamage;
    private final MutantBehaviour behaviour;
    private final float wanderRadius;

    MutantType(String displayName, int maxHealth, float speed, int baseDamage, MutantBehaviour behaviour, float wanderRadius) {
        this.displayName = displayName;
        this.maxHealth = maxHealth;
        this.speed = speed;
        this.baseDamage = baseDamage;
        this.behaviour = behaviour;
        this.wanderRadius = wanderRadius;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public float getSpeed() {
        return speed;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public MutantBehaviour getBehaviour() {
        return behaviour;
    }

    public float getWanderRadius() {
        return wanderRadius;
    }
}
