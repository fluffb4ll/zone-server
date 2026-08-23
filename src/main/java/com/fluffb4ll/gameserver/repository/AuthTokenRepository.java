package com.fluffb4ll.gameserver.repository;

import com.fluffb4ll.gameserver.entity.AuthTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthTokenEntity, UUID> {
    Optional<AuthTokenEntity> findByToken(UUID token);
    Optional<AuthTokenEntity> findTokenById(UUID id);
}
