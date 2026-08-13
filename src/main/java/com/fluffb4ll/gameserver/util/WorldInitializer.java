package com.fluffb4ll.gameserver.util;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.engine.GameLoop;
import com.fluffb4ll.gameserver.engine.factories.AnomalyFactory;
import com.fluffb4ll.gameserver.engine.factories.MutantFactory;
import com.fluffb4ll.gameserver.engine.factories.SpawnerFactory;
import com.fluffb4ll.gameserver.model.*;
import com.fluffb4ll.gameserver.model.enums.AnomalyType;
import com.fluffb4ll.gameserver.model.enums.MutantType;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class WorldInitializer {
    // TODO: убрать, когда напишу подгрузку состояния из бд
    private final MutantFactory mutantFactory;
    private final AnomalyFactory anomalyFactory;

    private final SpawnerFactory spawnerFactory;

    private final WorldManager worldManager;
    private final GameLoop gameLoop;
    private EventBus eventBus;


    public WorldInitializer(EventBus eventBus,
                            WorldManager worldManager,
                            GameLoop gameLoop,
                            MutantFactory mutantFactory,
                            AnomalyFactory anomalyFactory,
                            SpawnerFactory spawnerFactory) {
        this.eventBus = eventBus;
        this.worldManager = worldManager;
        this.gameLoop = gameLoop;
        this.mutantFactory = mutantFactory;
        this.anomalyFactory = anomalyFactory;
        this.spawnerFactory = spawnerFactory;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {

//        mutantFactory.create(MutantType.BLIND_DOG, new Vector2D(70f, 25f));
//        mutantFactory.create(MutantType.BLOODSUCKER, new Vector2D(50f, 50f));
//        mutantFactory.create(MutantType.FLESH, new Vector2D(30f, 30f));

//        anomalyFactory.create(AnomalyType.ELECTRO, new Vector2D(75f, 30f));
//        anomalyFactory.create(AnomalyType.GAS_CLOUD, new Vector2D(40f, 15f));
//        anomalyFactory.create(AnomalyType.VORTEX, new Vector2D(40f, 40f));

        spawnerFactory.spawnNest("Test Nest", new Vector2D(70f, 25f), 15f, MutantType.BLIND_DOG, 3);
        spawnerFactory.spawnAnomalies("Test Anomaly Belt", new Vector2D(50f, 40f), 20f, AnomalyType.VORTEX, 10);
        gameLoop.start();
    }
}