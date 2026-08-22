package com.fluffb4ll.gameserver.service;

import com.fluffb4ll.gameserver.entity.AuthTokenEntity;
import com.fluffb4ll.gameserver.entity.PlayerEntity;
import com.fluffb4ll.gameserver.repository.AuthTokenRepository;
import com.fluffb4ll.gameserver.repository.PlayerRepository;
import com.fluffb4ll.gameserver.util.RegexValidator;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<UUID> login(String nickname, String rawPassword) throws SecurityException {
        PlayerEntity player = playerRepository.findByNickname(nickname)
                .orElseThrow(() -> new SecurityException("Wrong credentials"));
        if (!passEncoder.matches(rawPassword, player.getPassword()))
            throw new SecurityException("Wrong credentials");

        UUID token = UUID.randomUUID();
        AuthTokenEntity authTokenEntity = new AuthTokenEntity(player.getId(), token);
        tokenRepository.save(authTokenEntity);
        return List.of(player.getId(), token);
    }

    @Transactional
    public UUID signup(String nickname, String rawPassword) {
        if (!RegexValidator.isValidPassword(rawPassword))
            throw new SecurityException(
                    "Invalid password. Password must be at least 8 characters long and contain " +
                    "digits and Latin letters");
        if (!RegexValidator.isValidNickname(nickname))
            throw new SecurityException("Invalid nickname");
        if (playerRepository.findByNickname(nickname).isPresent())
            throw new SecurityException("Player already exists");

        String encodedPassword = passEncoder.encode(rawPassword);
        PlayerEntity player = new PlayerEntity(encodedPassword, nickname);
        playerRepository.save(player);
        return player.getId();
    }

    @Transactional
    public boolean verifyAuthToken(UUID playerId, UUID receivedAT) {
        UUID storedAT = tokenRepository.findTokenById(playerId).orElse(null);
        if (storedAT == null)
            return false;
        return storedAT.equals(receivedAT);
    }
}
