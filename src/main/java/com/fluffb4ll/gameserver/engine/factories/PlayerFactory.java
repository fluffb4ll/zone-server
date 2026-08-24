package com.fluffb4ll.gameserver.engine.factories;

import com.fluffb4ll.gameserver.engine.EventBus;
import com.fluffb4ll.gameserver.engine.WorldManager;
import com.fluffb4ll.gameserver.engine.entities.Player;
import com.fluffb4ll.gameserver.entity.PlayerEntity;
import com.fluffb4ll.gameserver.model.PacketOpcodes;
import com.fluffb4ll.gameserver.repository.PlayerRepository;
import com.fluffb4ll.gameserver.util.Vector2D;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PlayerFactory extends EntityFactory {
    private final PlayerRepository playerRepository;
    private static final Vector2D STARTING_POS = new Vector2D(50f,50f);

    public PlayerFactory(WorldManager worldManager, EventBus eventBus, PlayerRepository playerRepository) {
        super(worldManager, eventBus);
        this.playerRepository = playerRepository;
    }

    @Transactional
    public Player create(UUID id) {
        PlayerEntity entity = playerRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Player not found")
        );
        if (!entity.hasPlayedBefore()) {
            entity.setHasPlayedBefore(true);
            entity.setMaxHealth(100);
            entity.setCurrHealth(100);
            entity.setPos(STARTING_POS);
            playerRepository.save(entity);
        }

        return new Player(
                id,
                entity.getPos(),
                entity.getNickname(),
                100,
                entity.getCurrHealth(),
                15,
                5f,
                eventBus
        );
    }

    @Transactional
    public Player spawn(UUID id) {
        Player player = create(id);
        worldManager.spawnEntity(player);
        return player;
    }
}
