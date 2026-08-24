package com.fluffb4ll.gameserver.repository;

import com.fluffb4ll.gameserver.entity.PlayerAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<PlayerAuthEntity, UUID> {
    Optional<PlayerAuthEntity> findByToken(UUID token);
    Optional<PlayerAuthEntity> findTokenById(UUID id);
    Optional<PlayerAuthEntity> findByNickname(String nickname);
}
