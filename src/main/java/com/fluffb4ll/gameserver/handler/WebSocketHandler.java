package com.fluffb4ll.gameserver.handler;

import com.fluffb4ll.gameserver.engine.WorldManager;
import com.fluffb4ll.gameserver.engine.entities.Player;
import com.fluffb4ll.gameserver.engine.factories.PlayerFactory;
import com.fluffb4ll.gameserver.entity.PlayerEntity;
import com.fluffb4ll.gameserver.model.PacketOpcodes;
import com.fluffb4ll.gameserver.model.records.commands.AttackCommand;
import com.fluffb4ll.gameserver.model.records.commands.MoveCommand;
import com.fluffb4ll.gameserver.repository.PlayerRepository;
import com.fluffb4ll.gameserver.service.PlayerAuthService;
import com.fluffb4ll.gameserver.util.ByteParser;
import com.fluffb4ll.gameserver.util.Vector2D;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends BinaryWebSocketHandler {
    private final Map<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final List<UUID> deadSessions = new ArrayList<>();
    private final WorldManager worldManager;
    private final PlayerAuthService authService;
    private final PlayerFactory factory;
    private final PlayerRepository playerRepository;


    public WebSocketHandler(WorldManager worldManager, PlayerAuthService authService, PlayerFactory factory, PlayerRepository playerRepository) {
        this.worldManager = worldManager;
        this.authService = authService;
        this.factory = factory;
        this.playerRepository = playerRepository;
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        Player player = (Player) session.getAttributes().get("player");
        if (player == null) {
            handlePlayerLogin(session, message);
            return;
        }

        ByteBuffer buffer = message.getPayload();
        if (buffer.limit() == 0)
            return;

        byte opcode = buffer.get();

        switch (opcode) {
            case PacketOpcodes.OP_C2S_MOVE -> handleMovePacket(player, buffer);
            case PacketOpcodes.OP_C2S_ATTACK -> handleAttackPacket(player, buffer);
            default -> System.err.println("Unknown opcode: " + opcode);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        UUID id = extractPlayerId(session);
        if (id == null)
            try {
                session.close();
                return;
            } catch (IOException _) {}

        WebSocketSession threadSafeSesh =
                new ConcurrentWebSocketSessionDecorator(session, 5000, 8192);
        sessions.put(id, threadSafeSesh);
        session.getAttributes().put("id", id);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Player player = (Player) session.getAttributes().get("player");
        if (player == null)
            return;
        UUID id = player.getUuid();

        PlayerEntity playerData = playerRepository.findById(id).orElse(null);
        if (playerData != null) {
            playerData.copyPlayerData(player);
            playerRepository.save(playerData);
        }

        sessions.remove(id);
        worldManager.removePlayer(id);
    }

    // дёргает айдишник из query (параметр id)
    private UUID extractPlayerId(WebSocketSession session) {
        String id = UriComponentsBuilder.fromUri(session.getUri())
                .build().getQueryParams().getFirst("id");

        if (id == null)
            return null;
        return UUID.fromString(id);
    }

    private void handlePlayerLogin(WebSocketSession session, BinaryMessage message) {
        UUID playerId = (UUID) session.getAttributes().get("id");

        try {
            ByteBuffer buffer = message.getPayload();
            byte opcode = buffer.get();
            UUID token = ByteParser.parseUUID(buffer);

            if (opcode != PacketOpcodes.OP_C2S_AUTH || !authService.verifyAuthToken(playerId, token)) {
                session.close();
                return;
            }

            Player player = factory.spawn(playerId);
            session.getAttributes().put("player", player);
        } catch (Exception _) {
            addDeadSession(playerId);
        }
    }

    private void handleMovePacket(Player player, ByteBuffer buffer) {
        long packetId = buffer.getLong();
        float x = buffer.getFloat();
        float y = buffer.getFloat();

        player.addToInboundQueue(new MoveCommand(packetId, new Vector2D(x, y)));
    }

    private void handleAttackPacket(Player player, ByteBuffer buffer) {
        long packetId = buffer.getLong();
        try {
            UUID targetId = ByteParser.parseUUID(buffer);
            player.addToInboundQueue(new AttackCommand(packetId, targetId));
        } catch (IllegalArgumentException e) { throw new IllegalArgumentException(e); }
    }

    public Map<UUID, WebSocketSession> getSessions() {
        return Map.copyOf(sessions);
    }

    public void addDeadSession(UUID sessionId) {
        deadSessions.add(sessionId);
    }

    public void closeDeadSessions() {
        for (UUID id : deadSessions) {
            WebSocketSession session = sessions.remove(id);
            try {
                session.close();
            } catch (IOException _) {}
        }
    }
}
