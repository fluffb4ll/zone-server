package com.fluffb4ll.gameserver.entity;

import com.fluffb4ll.gameserver.engine.entities.Player;
import com.fluffb4ll.gameserver.util.IdGeneratorUtil;
import com.fluffb4ll.gameserver.util.Vector2D;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "players")
public class PlayerEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "nickname", length = 16, nullable = false, unique = true)
    private String nickname;

    @Column(name = "has_played_before")
    private boolean hasPlayedBefore;

    @Column(name = "max_health")
    private int maxHealth;

    @Column(name = "curr_health")
    private int currHealth;

    @Column(name = "speed")
    private float speed;

    @Column(name = "pos_x")
    private float posX;

    @Column(name = "pos_y")
    private float posY;

    public PlayerEntity() {}

    public PlayerEntity(UUID id,
                        String nickname,
                        int maxHealth,
                        int currHealth,
                        float speed,
                        Vector2D pos)
    {
        this.id = id;
        this.nickname = nickname;
        this.maxHealth = maxHealth;
        this.currHealth = currHealth;
        this.speed = speed;
        posX = pos.x;
        posY = pos.y;
        hasPlayedBefore = true;
    }

    public PlayerEntity(UUID id, String nickname)
    {
        this.id = id;
        this.nickname = nickname;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String displayName) {
        this.nickname = displayName;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getCurrHealth() {
        return currHealth;
    }

    public void setCurrHealth(int currHealth) {
        this.currHealth = currHealth;
    }

    public Vector2D getPos() {
        return new Vector2D(posX, posY);
    }

    public void setPos(Vector2D pos) {
        posX = pos.x;
        posY = pos.y;
    }

    public boolean hasPlayedBefore() {
        return hasPlayedBefore;
    }

    public void setHasPlayedBefore(boolean hasPlayedBefore) {
        this.hasPlayedBefore = hasPlayedBefore;
    }

    public void copyPlayerData(Player player) {
        nickname = player.getDisplayName();
        maxHealth = player.getMaxHealth();
        currHealth = player.getHealth();
        speed = player.getSpeed();
        Vector2D currPos = player.getPosition();
        posX = currPos.x;
        posY = currPos.y;
    }
}
