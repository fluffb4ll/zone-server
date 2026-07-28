package com.fluffb4ll.gameserver.model;

import com.fluffb4ll.gameserver.model.enums.MutantBehaviour;
import com.fluffb4ll.gameserver.model.enums.MutantState;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Mutant extends LivingEntity {
    private final String species;
    private MutantBehaviour behaviour;

    private MutantState state;

    public Mutant(UUID uuid,
                  AtomicInteger maxHealth,
                  AtomicInteger damage,
                  EventBus eventBus,
                  String species,
                  MutantBehaviour behaviour) {
        super(uuid, maxHealth, damage, eventBus);

        this.species = species;
        this.behaviour = behaviour;
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
}
