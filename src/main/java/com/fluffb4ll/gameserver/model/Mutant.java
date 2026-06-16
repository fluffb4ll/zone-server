package com.fluffb4ll.gameserver.model;

import com.fluffb4ll.gameserver.model.enums.MutantBehaviour;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Mutant extends LivingEntity {
    private final String species;
    private MutantBehaviour behaviour;


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
}
