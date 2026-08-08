package com.fluffb4ll.gameserver.util;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.engine.GameLoop;
import com.fluffb4ll.gameserver.model.*;
import com.fluffb4ll.gameserver.model.enums.AnomalyType;
import com.fluffb4ll.gameserver.model.enums.MutantBehaviour;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class WorldInitializer {

    private final WorldManager worldManager;
    private final GameLoop gameLoop;

    @Autowired
    private EventBus eventBus;

    public WorldInitializer(WorldManager worldManager, GameLoop gameLoop) {
        this.worldManager = worldManager;
        this.gameLoop = gameLoop;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        Mutant testMutant = new Mutant(
                new Vector2D(50.0f, 50.0f),
                100,
                15,
                1,
                eventBus,
                "BlindDog",
                MutantBehaviour.NEUTRAL
        );

        Anomaly testAnomaly = new Anomaly(
                new Vector2D(53.0f, 50.0f),
                1000,
                50,
                0,
                eventBus,
                AnomalyType.THERMAL,
                3.0f,
                true
        );

        worldManager.spawnEntity(testMutant);
        worldManager.spawnEntity(testAnomaly);

        gameLoop.start();
    }
}