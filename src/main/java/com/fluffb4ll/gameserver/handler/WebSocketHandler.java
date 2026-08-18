package com.fluffb4ll.gameserver.handler;

import com.fluffb4ll.gameserver.engine.WorldManager;
import com.fluffb4ll.gameserver.engine.entities.LivingEntity;
import com.fluffb4ll.gameserver.engine.entities.Player;
import com.fluffb4ll.gameserver.engine.factories.PlayerFactory;
import com.fluffb4ll.gameserver.model.records.AttackCommand;
import com.fluffb4ll.gameserver.model.records.MoveCommand;
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

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends BinaryWebSocketHandler {
    private final Map<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final WorldManager worldManager;
    private final PlayerAuthService authService;
    private final PlayerFactory factory;

    // опкоды
    private final byte OP_AUTH = 0x00;
    private final byte OP_MOVE = 0x01;
    private final byte OP_ATTACK = 0x02;

    public WebSocketHandler(WorldManager worldManager, PlayerAuthService authService, PlayerFactory factory) {
        this.worldManager = worldManager;
        this.authService = authService;
        this.factory = factory;
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        Player player = (Player) session.getAttributes().get("player");
        if (!(boolean) session.getAttributes().get("isAuthenticated")) {
            handlePlayerLogin(session, message);
            return;
        }
        if (player == null)
            return;

        ByteBuffer buffer = message.getPayload();
        if (buffer.limit() == 0)
            return;

        byte opcode = buffer.get();

        switch (opcode) {
            case OP_MOVE -> handleMovePacket(player, buffer);
            case OP_ATTACK -> handleAttackPacket(player, buffer);
            default -> System.err.println("Unknown opcode: " + opcode);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        UUID id = extractPlayerId(session);
        WebSocketSession threadSafeSesh =
                new ConcurrentWebSocketSessionDecorator(session, 5000, 8192);
        sessions.put(id, threadSafeSesh);
        session.getAttributes().put("isAuthenticated", false);
        session.getAttributes().put("id", id);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        if (Boolean.FALSE.equals(session.getAttributes().get("isAuthenticated")))
            return;
        UUID id = ((Player) session.getAttributes().get("player")).getUuid();
        sessions.remove(id);
        worldManager.removePlayer(id);
    }

    // дёргает айдишник из query (параметр id)
    private UUID extractPlayerId(WebSocketSession session) {
        String query = session.getUri().getQuery();
        return UUID.fromString(UriComponentsBuilder.fromUri(session.getUri())
                .build().getQueryParams().getFirst("id"));
    }

    private void handlePlayerLogin(WebSocketSession session, BinaryMessage message) {
        try {
            ByteBuffer buffer = message.getPayload();
            byte opcode = buffer.get();
            UUID token = ByteParser.parseUUID(buffer);
            UUID playerId = (UUID) session.getAttributes().get("id");
            if (opcode != OP_AUTH || !authService.verifyAuthToken(playerId, token))
                session.close();

            Player player = factory.spawn(playerId);
            session.getAttributes().put("isAuthenticated", true);
            session.getAttributes().put("player", player);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
}
