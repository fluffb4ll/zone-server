package com.fluffb4ll.gameserver.model.records.commands;

public sealed interface PlayerCommand permits MoveCommand, AttackCommand {
    long packetId();
}
