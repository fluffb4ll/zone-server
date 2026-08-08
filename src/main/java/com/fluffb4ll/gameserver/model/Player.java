package com.fluffb4ll.gameserver.model;

import com.fluffb4ll.gameserver.engine.EventBus;

import java.util.UUID;

/**
 * Defines a player entity.
 */
public class Player extends LivingEntity {
    private volatile String nickname;

    public Player(Vector2D position, String nickname, int maxHealth, int damage, float speed, EventBus eventBus) {
        super(position, maxHealth, damage, speed, eventBus);
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    protected synchronized void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
