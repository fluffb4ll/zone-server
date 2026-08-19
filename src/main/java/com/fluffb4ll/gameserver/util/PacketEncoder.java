package com.fluffb4ll.gameserver.util;

import com.fluffb4ll.gameserver.engine.entities.LivingEntity;
import com.fluffb4ll.gameserver.engine.entities.Player;
import com.fluffb4ll.gameserver.handler.WebSocketHandler;

import java.nio.ByteBuffer;
import java.util.List;

public class PacketEncoder {
    /** <p>Создаёт индивидуальный снимок мира для отправки выбранному игроку.</p>
     * <p>Снимок содержит следующую информацию:<br>
     * 1. Опкод (1 байт)<br>
     * 2. {@code lastProcessedPacketId} (long, 8 байт)<br>
     * 3. Количество сущностей в снимке (int, 4 байта)<br>
     * 4. Массив сущностей:<br>
     * 4.1 UUID сущности (16 байт)<br>
     * 4.2, 4.3 Координаты X и Y (float x2, 8 байт)<br>
     * 4.4 {@code health} сущности (int, 4 байта)</p>*/
    public static byte[] createWorldSnapshot(Player recipient, List<LivingEntity> visibleEntities) {
        int packetSize = 1 + 8 + 4 + (visibleEntities.size() * 28);
        ByteBuffer buffer = ByteBuffer.allocate(packetSize);

        buffer.put(WebSocketHandler.S2C_OP_WORLD_SNAPSHOT);
        buffer.putLong(recipient.getLastProcessedPacketId());
        buffer.putInt(visibleEntities.size());

        for (LivingEntity entity : visibleEntities) {
            buffer.putLong(entity.getUuid().getMostSignificantBits());
            buffer.putLong(entity.getUuid().getLeastSignificantBits());

            Vector2D pos = entity.getPosition();
            buffer.putFloat(pos.x);
            buffer.putFloat(pos.y);

            buffer.putInt(entity.getHealth());
        }
        
        return buffer.array();
    }
}
