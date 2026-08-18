package com.fluffb4ll.gameserver.handler;

import com.fluffb4ll.gameserver.engine.WorldManager;
import com.fluffb4ll.gameserver.engine.entities.LivingEntity;
import com.fluffb4ll.gameserver.engine.entities.Player;
import com.fluffb4ll.gameserver.model.records.AttackCommand;
import com.fluffb4ll.gameserver.model.records.MoveCommand;
import com.fluffb4ll.gameserver.util.ByteParser;
import com.fluffb4ll.gameserver.util.Vector2D;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends BinaryWebSocketHandler {
    private final Map<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final WorldManager worldManager;

    // опкоды
    private final byte OP_MOVE = 0x01;
    private final byte OP_ATTACK = 0x02;

    public WebSocketHandler(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        Player player = (Player) session.getAttributes().get("player");
        if (player == null)
            return;

        ByteBuffer buffer = message.getPayload();
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
        sessions.put(id, session);
        // TODO: заспавнить игрока в мире на его предыдущей позиции, подгрузив данные из бд,
        //  отправить его данные обратно
        // Player player = ...
        // session.getAttributes().put("player", player);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        UUID id = extractPlayerId(session);
        sessions.remove(id);
        //worldManager.removePlayer(id);
    }

    // дёргает айдишник из query (параметр id)
    private UUID extractPlayerId(WebSocketSession session) {
        char[] query = session.getUri().getQuery().toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i < 39; i++) {
            sb.append(query[i]);
        }
        return UUID.fromString(sb.toString());
    }

    private void handleMovePacket(Player player, ByteBuffer buffer) {
        long packetId = buffer.getLong();
        float x = buffer.getFloat();
        float y = buffer.getFloat();

        player.addToInboundQueue(new MoveCommand(packetId, new Vector2D(x, y)));
    }

    private void handleAttackPacket(Player player, ByteBuffer buffer) {
        long packetId = buffer.getLong();
        UUID targetId = ByteParser.parseUUID(buffer);
        LivingEntity target = worldManager.findLivingEntityInNearbyChunks(player, targetId);
        if (target != null)
            player.addToInboundQueue(new AttackCommand(packetId, target));
    }
}
