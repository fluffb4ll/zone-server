package com.fluffb4ll.gameserver.service;

import com.fluffb4ll.gameserver.entity.PlayerEntity;
import com.fluffb4ll.gameserver.repository.PlayerRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerSaveService {
    private final PlayerRepository playerRepository;

    public PlayerSaveService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Async
    public void savePlayerDataAsync(List<PlayerEntity> playerData) {
        // TODO: пися попа
    }
}
