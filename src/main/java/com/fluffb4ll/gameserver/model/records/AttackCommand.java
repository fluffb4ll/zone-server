package com.fluffb4ll.gameserver.model.records;

import com.fluffb4ll.gameserver.engine.entities.LivingEntity;
import com.fluffb4ll.gameserver.model.interfaces.PlayerCommand;

public record AttackCommand(long packetId, LivingEntity target) implements PlayerCommand {
}
