package com.fluffb4ll.gameserver.model.records.events;

import com.fluffb4ll.gameserver.util.Vector2D;

import java.util.UUID;

public sealed interface EntityEvent permits EntityDeathEvent {
    UUID uuid();
    Vector2D pos();
}
