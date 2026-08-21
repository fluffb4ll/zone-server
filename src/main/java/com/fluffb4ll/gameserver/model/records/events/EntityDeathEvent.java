package com.fluffb4ll.gameserver.model.records.events;

import com.fluffb4ll.gameserver.model.records.commands.PlayerCommand;
import com.fluffb4ll.gameserver.util.Vector2D;

import java.util.UUID;

public record EntityDeathEvent(UUID uuid, Vector2D pos) implements EntityEvent { }