package com.fluffb4ll.gameserver.engine.entities;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.model.interfaces.PlayerCommand;
import com.fluffb4ll.gameserver.util.Vector2D;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Defines a player entity.
 */
public class Player extends LivingEntity {
    private final AtomicLong lastProcessedPacketId = new AtomicLong(0);
    private final Queue<PlayerCommand> inboundQueue = new ConcurrentLinkedQueue<>();

    public Player(UUID id,
                  Vector2D position,
                  String displayName,
                  int maxHealth,
                  int damage,
                  float speed,
                  EventBus eventBus) {
        super(id, position, displayName, maxHealth, damage, speed, eventBus);
    }


    public Queue<PlayerCommand> getInboundQueue() {
        return inboundQueue;
    }

    public void addToInboundQueue(PlayerCommand command) {
        inboundQueue.add(command);
    }
}
