package com.fluffb4ll.gameserver.service;

import com.fluffb4ll.gameserver.entity.PlayerAuthEntity;
import com.fluffb4ll.gameserver.entity.PlayerEntity;
import com.fluffb4ll.gameserver.repository.AuthRepository;
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
    private final AuthRepository authRepository;
    private final PasswordEncoder passEncoder;

    public PlayerAuthService(PlayerRepository playerRepository,
                             AuthRepository authRepository,
                             PasswordEncoder passEncoder) {
        this.playerRepository = playerRepository;
        this.authRepository = authRepository;
        this.passEncoder = passEncoder;
    }

    @Transactional
    public List<UUID> login(String nickname, String rawPassword) throws SecurityException {
        PlayerAuthEntity player = authRepository.findByNickname(nickname)
                .orElseThrow(() -> new SecurityException("Wrong credentials"));
        if (!passEncoder.matches(rawPassword, player.getPassword()))
            throw new SecurityException("Wrong credentials");

        UUID token = UUID.randomUUID();
        player.setToken(token);
        authRepository.save(player);
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
        if (authRepository.findByNickname(nickname).isPresent())
            throw new SecurityException("Player already exists");

        String encodedPassword = passEncoder.encode(rawPassword);
        PlayerAuthEntity player = new PlayerAuthEntity(encodedPassword, nickname);
        authRepository.save(player);
        playerRepository.save(new PlayerEntity(player.getId(), nickname));
        return player.getId();
    }

    @Transactional
    public boolean verifyAuthToken(UUID playerId, UUID receivedAT) {
        PlayerAuthEntity storedAT = authRepository.findTokenById(playerId).orElse(null);
        if (storedAT == null)
            return false;
        return storedAT.getToken().equals(receivedAT);
    }
}
