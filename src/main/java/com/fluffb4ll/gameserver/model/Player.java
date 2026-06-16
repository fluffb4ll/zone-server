package com.fluffb4ll.gameserver.model;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Defines a player entity.
 */
public class Player extends LivingEntity {
    private volatile String nickname;

    public Player(UUID uuid, String nickname, AtomicInteger maxHealth, AtomicInteger damage, EventBus eventBus) {
        super(uuid, maxHealth, damage, eventBus);
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    protected synchronized void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
