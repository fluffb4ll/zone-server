package com.fluffb4ll.gameserver.model.database.entities;

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

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", length = 16, nullable = false, unique = true)
    private String displayName;

    @Column(name = "max_health")
    private int maxHealth;

    @Column(name = "curr_health")
    private int currHealth;

    @Column(name = "pos_x")
    private float posX;

    @Column(name = "pos_y")
    private float posY;

    public PlayerEntity() {}

    public PlayerEntity(UUID id,
                        String password,
                        String displayName,
                        int maxHealth,
                        int currHealth,
                        Vector2D pos)
    {
        this.id = id;
        this.password = password;
        this.displayName = displayName;
        this.maxHealth = maxHealth;
        this.currHealth = currHealth;
        posX = pos.x;
        posY = pos.y;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
}
