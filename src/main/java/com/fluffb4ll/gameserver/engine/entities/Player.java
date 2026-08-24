package com.fluffb4ll.gameserver.engine.entities;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.engine.WorldManager;
import com.fluffb4ll.gameserver.model.records.commands.PlayerCommand;
import com.fluffb4ll.gameserver.model.records.commands.AttackCommand;
import com.fluffb4ll.gameserver.model.records.commands.MoveCommand;
import com.fluffb4ll.gameserver.util.Vector2D;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Defines a player entity.
 */
public class Player extends LivingEntity {
    private final AtomicLong lastProcessedPacketId = new AtomicLong(0);
    private final Queue<PlayerCommand> inboundQueue = new ConcurrentLinkedQueue<>();
    private final Queue<byte[]> outboundEventsQueue = new ConcurrentLinkedQueue<>();

    public Player(UUID id,
                  Vector2D position,
                  String displayName,
                  int maxHealth,
                  int damage,
                  float speed,
                  EventBus eventBus) {
        super(null, id, position, displayName, maxHealth, damage, speed, eventBus);
    }

    public Player(UUID id,
                  Vector2D position,
                  String displayName,
                  int maxHealth,
                  int currHealth,
                  int damage,
                  float speed,
                  EventBus eventBus) {
        super(null, id, position, displayName, maxHealth, damage, speed, eventBus);

        setHealth(currHealth);
    }

    public long getLastProcessedPacketId() {
        return lastProcessedPacketId.get();
    }

    public Queue<PlayerCommand> getInboundQueue() {
        return inboundQueue;
    }

    public void addToInboundQueue(PlayerCommand command) {
        inboundQueue.add(command);
    }

    public void processInboundQueue(WorldManager worldManager) {
        PlayerCommand command;

        while ((command = inboundQueue.poll()) != null) {
            if (command.packetId() <= lastProcessedPacketId.get())
                continue;

            if (command instanceof MoveCommand(long packetId, Vector2D pos))
                handleMoveCommand(worldManager, (MoveCommand) command);
            else if (command instanceof AttackCommand)
                handleAttackCommand(worldManager, (AttackCommand) command);

            lastProcessedPacketId.set(command.packetId());
        }
    }

    private void handleMoveCommand(WorldManager worldManager, MoveCommand command) {
        move(command.pos());
        System.err.println(getUuid() + " moved! New pos: " + getPosition().x + " " + getPosition().y);
    }

    private void handleAttackCommand(WorldManager worldManager, AttackCommand command) {
        LivingEntity target = worldManager.findLivingEntityInNearbyChunks(this, command.targetId());
        if (target != null)
            target.takeDamage(getDamage());
    }

    public Queue<byte[]> getOutboundEventsQueue() {
        return outboundEventsQueue;
    }

    public void addToOutboundQueue(byte[] packet) {
        outboundEventsQueue.add(packet);
    }
}
