package com.fluffb4ll.gameserver.util;

import com.fluffb4ll.gameserver.engine.entities.Anomaly;
import com.fluffb4ll.gameserver.engine.entities.LivingEntity;
import com.fluffb4ll.gameserver.engine.entities.Mutant;
import com.fluffb4ll.gameserver.engine.entities.Player;
import com.fluffb4ll.gameserver.handler.WebSocketHandler;
import com.fluffb4ll.gameserver.model.records.events.EntityDeathEvent;

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
     * 4.2 Тип сущности (1 байт)
     * 4.3, 4.4 Координаты X и Y (float x2, 8 байт)<br>
     * 4.5 {@code health} сущности (int, 4 байта)</p>*/
    public static byte[] createWorldSnapshot(Player recipient, List<LivingEntity> visibleEntities) {
        int packetSize = 1 + 8 + 4 + (visibleEntities.size() * 28);
        ByteBuffer buffer = ByteBuffer.allocate(packetSize);

        buffer.put(WebSocketHandler.OP_S2C_WORLD_SNAPSHOT);
        buffer.putLong(recipient.getLastProcessedPacketId());
        buffer.putInt(visibleEntities.size());

        for (LivingEntity entity : visibleEntities) {
            buffer.putLong(entity.getUuid().getMostSignificantBits());
            buffer.putLong(entity.getUuid().getLeastSignificantBits());

            switch (entity) {
                case Player player -> buffer.put(WebSocketHandler.OP_ENTITY_TYPE_PLAYER);
                case Mutant mutant -> buffer.put(WebSocketHandler.OP_ENTITY_TYPE_MUTANT);
                case Anomaly anomaly -> buffer.put(WebSocketHandler.OP_ENTITY_TYPE_ANOMALY);
                default -> throw new IllegalArgumentException("Unknown entity type: " + entity.getClass().getName());
            }

            Vector2D pos = entity.getPosition();
            buffer.putFloat(pos.x);
            buffer.putFloat(pos.y);

            buffer.putInt(entity.getHealth());
        }

        return buffer.array();
    }

    /** <p>Формирует тело пакета для отправки {@code EntityDeathEvent} выбранному игроку</p>
     * <p>Тело пакета:<br>
     * 1. Опкод (1 байт)
     * 2. UUID сущности (16 байт)</p>*/
    public static byte[] encodeDeathEvent(EntityDeathEvent event) {
        int packetSize = 1 + 16;
        ByteBuffer buffer = ByteBuffer.allocate(packetSize);

        buffer.put(WebSocketHandler.OP_S2C_ENTITY_DEATH);
        buffer.putLong(event.uuid().getMostSignificantBits());
        buffer.putLong(event.uuid().getLeastSignificantBits());

        return buffer.array();
    }
}
