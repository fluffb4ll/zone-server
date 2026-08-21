package com.fluffb4ll.gameserver.model.records.commands;

import java.util.UUID;

public record AttackCommand(long packetId, UUID targetId) implements PlayerCommand {
}
