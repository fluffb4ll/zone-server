package com.fluffb4ll.gameserver.model.records;

import com.fluffb4ll.gameserver.engine.entities.LivingEntity;

import java.util.UUID;

public record AttackCommand(long packetId, UUID targetId) implements PlayerCommand {
}
