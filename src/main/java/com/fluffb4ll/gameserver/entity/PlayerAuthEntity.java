package com.fluffb4ll.gameserver.entity;

import com.fluffb4ll.gameserver.util.IdGeneratorUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "accounts")
public class PlayerAuthEntity {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "token", unique = true)
    private UUID token;

    @Column(name = "password", length = 60, nullable = false)
    private String password;

    @Column(name = "nickname", length = 16, nullable = false, unique = true)
    private String nickname;

    public PlayerAuthEntity() {}

    public PlayerAuthEntity(UUID id, UUID token, String password, String nickname) {
        this.id = id;
        this.token = token;
        this.password = password;
        this.nickname = nickname;
    }

    public PlayerAuthEntity(String password, String nickname) {
        id = IdGeneratorUtil.generateId();
        this.password = password;
        this.nickname = nickname;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
