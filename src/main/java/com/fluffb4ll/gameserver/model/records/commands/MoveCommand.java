package com.fluffb4ll.gameserver.model.records.commands;

import com.fluffb4ll.gameserver.util.Vector2D;

public record MoveCommand(long packetId, Vector2D pos) implements PlayerCommand {
}
