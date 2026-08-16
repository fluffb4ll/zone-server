package com.fluffb4ll.gameserver.repository;

import com.fluffb4ll.gameserver.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, UUID> {
    Optional<PlayerEntity> findById(UUID id);
    Optional<PlayerEntity> findByNickname(String nickname);
}
