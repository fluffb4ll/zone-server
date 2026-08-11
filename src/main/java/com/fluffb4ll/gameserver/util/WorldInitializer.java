package com.fluffb4ll.gameserver.util;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.engine.GameLoop;
import com.fluffb4ll.gameserver.engine.factories.AnomalyFactory;
import com.fluffb4ll.gameserver.engine.factories.MutantFactory;
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

    private final WorldManager worldManager;
    private final GameLoop gameLoop;
    private EventBus eventBus;


    public WorldInitializer(EventBus eventBus,
                            WorldManager worldManager,
                            GameLoop gameLoop,
                            MutantFactory mutantFactory,
                            AnomalyFactory anomalyFactory) {
        this.eventBus = eventBus;
        this.worldManager = worldManager;
        this.gameLoop = gameLoop;
        this.mutantFactory = mutantFactory;
        this.anomalyFactory = anomalyFactory;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
//        mutantFactory.create(MutantType.BLIND_DOG, new Vector2D(70f, 25f));
//        mutantFactory.create(MutantType.BLOODSUCKER, new Vector2D(50f, 50f));
//        mutantFactory.create(MutantType.FLESH, new Vector2D(30f, 30f));

//        anomalyFactory.create(AnomalyType.ELECTRO, new Vector2D(75f, 30f));
//        anomalyFactory.create(AnomalyType.GAS_CLOUD, new Vector2D(40f, 15f));
//        anomalyFactory.create(AnomalyType.VORTEX, new Vector2D(40f, 40f));

        gameLoop.start();
    }
}