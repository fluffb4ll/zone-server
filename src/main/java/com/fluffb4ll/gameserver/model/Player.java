package com.fluffb4ll.gameserver.model;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Defines a player entity.
 */
public class Player extends LivingEntity {
    private volatile String nickname;

    public Player(UUID uuid, Vector2D position, String nickname, int maxHealth, int damage, EventBus eventBus) {
        super(uuid, position, maxHealth, damage, eventBus);
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    protected synchronized void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
