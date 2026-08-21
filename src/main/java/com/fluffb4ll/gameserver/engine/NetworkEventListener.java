package com.fluffb4ll.gameserver.engine;

import com.fluffb4ll.gameserver.engine.entities.Player;
import com.fluffb4ll.gameserver.model.records.events.EntityDeathEvent;
import com.fluffb4ll.gameserver.model.records.events.EntityEvent;
import com.fluffb4ll.gameserver.util.PacketEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class NetworkEventListener {
    private final WorldManager worldManager;
    private final EventBus eventBus;

    public NetworkEventListener(WorldManager worldManager, EventBus eventBus) {
        this.worldManager = worldManager;
        this.eventBus = eventBus;

        eventBus.subscribe(EntityDeathEvent.class, this::handleEntityEvent);
    }

    /** Обрабатывает события сущностей, записывая соответствующие пакеты в {@code outboundEventQueue} клиента.
     * @param event Событие сущности (см. {@link EntityEvent}) */
    private void handleEntityEvent(EntityEvent event) {
        byte[] packet;
        var players = new ArrayList<Player>();

        for (MapChunk chunk : worldManager.getNearbyChunks(event.pos()))
            players.addAll(chunk.getPlayers());

        if (players.isEmpty())
            return;

        if (event instanceof EntityDeathEvent deathEvent)
             packet = PacketEncoder.encodeDeathEvent(deathEvent);
        else
            throw new IllegalArgumentException("Incorrect event type");

        for (Player player : players)
            player.addToOutboundQueue(packet);
    }
}
