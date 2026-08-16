package com.fluffb4ll.gameserver.engine;

import com.fluffb4ll.gameserver.model.database.entities.AuthTokenEntity;
import com.fluffb4ll.gameserver.model.database.entities.PlayerEntity;
import com.fluffb4ll.gameserver.model.database.repositories.AuthTokenRepository;
import com.fluffb4ll.gameserver.model.database.repositories.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlayerAuthService {
    private final PlayerRepository playerRepository;
    private final AuthTokenRepository tokenRepository;
    private final PasswordEncoder passEncoder;

    public PlayerAuthService(PlayerRepository playerRepository,
                             AuthTokenRepository tokenRepository,
                             PasswordEncoder passEncoder) {
        this.playerRepository = playerRepository;
        this.tokenRepository = tokenRepository;
        this.passEncoder = passEncoder;
    }

    @Transactional
    public UUID login(String nickname, String rawPassword) throws SecurityException {
        PlayerEntity player = playerRepository.findByNickname(nickname)
                .orElseThrow(() -> new SecurityException("Wrong credentials"));
        if (!passEncoder.matches(rawPassword, player.getPassword()))
            throw new SecurityException("Wrong credentials");

        UUID token = UUID.randomUUID();
        AuthTokenEntity authTokenEntity = new AuthTokenEntity(player.getId(), token);
        tokenRepository.save(authTokenEntity);
        return token;
    }
}
