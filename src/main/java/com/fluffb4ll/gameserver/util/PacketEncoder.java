package com.fluffb4ll.gameserver.util;

import com.fluffb4ll.gameserver.engine.entities.Anomaly;
import com.fluffb4ll.gameserver.engine.entities.LivingEntity;
import com.fluffb4ll.gameserver.engine.entities.Mutant;
import com.fluffb4ll.gameserver.engine.entities.Player;
import com.fluffb4ll.gameserver.model.PacketOpcodes;
import com.fluffb4ll.gameserver.model.records.events.EntityDeathEvent;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PacketEncoder {
    /** <p>Создаёт индивидуальный снимок мира для отправки выбранному игроку.</p>
     * <p>Снимок содержит следующую информацию:<br>
     * 1. Опкод (1 байт)<br>
     * 2. {@code lastProcessedPacketId} ({@code long}, 8 байт)<br>
     * 3. Количество сущностей в снимке ({@code int}, 4 байта)<br>
     * 4. Массив сущностей:<br>
     * 4.1 UUID сущности (16 байт)<br>
     * 4.2, 4.3 Координаты X и Y ({@code float} x2, 8 байт)<br>
     * 4.4 {@code health} сущности ({@code int}, 4 байта)<br>
     * 4.5 Опкод типа сущности (1 байт)
     * 4.6 Иные данные, специфичные для определённого типа сущности</p>
     * @param recipient Клиент-получатель пакета
     * @param visibleEntities Видимые клиентом сущности
     * @return Пакет - массив байтов, репрезентующий текущее состояние мира для выбранного игрока */
    public static byte[] createWorldSnapshot(Player recipient, List<LivingEntity> visibleEntities) {
        int packetSize = 1 + 8 + 4 + (visibleEntities.size() * 28);
        ByteBuffer buffer = ByteBuffer.allocate(packetSize);

        buffer.put(PacketOpcodes.OP_S2C_WORLD_SNAPSHOT);
        buffer.putLong(recipient.getLastProcessedPacketId());
        buffer.putInt(visibleEntities.size());

        for (LivingEntity entity : visibleEntities) {
            buffer.putLong(entity.getUuid().getMostSignificantBits());
            buffer.putLong(entity.getUuid().getLeastSignificantBits());

            Vector2D pos = entity.getPosition();
            buffer.putFloat(pos.x);
            buffer.putFloat(pos.y);

            buffer.putInt(entity.getHealth());

            switch (entity) {
                case Player player -> putPlayerData(buffer, player);
                case Mutant mutant -> putMutantData(buffer, mutant);
                case Anomaly anomaly -> putAnomalyData(buffer, anomaly);
                default -> throw new IllegalArgumentException("Unknown entity type: " + entity.getClass().getName());
            }
        }

        return buffer.array();
    }

    /** <p>Вставляет данные сущности-игрока в {@link ByteBuffer} снапшота:<br>
     * 1. Опкод типа сущности (1 байт)<br>
     * 2. Максимальное здоровья игрока ({@code int}, 4 байта)<br>
     * 3. Скорость игрока ({@code float}, 8 байт)<br>
     * 4. Ник игрока ({@code String}, 1-16 байт)</p>
     * @param buffer {@link ByteBuffer}, в который ведётся запись
     * @param player Игрок - элемент снапшота*/
    private static void putPlayerData(ByteBuffer buffer, Player player) {
        buffer.put(PacketOpcodes.OP_ENTITY_TYPE_PLAYER);
        buffer.putInt(player.getMaxHealth());
        buffer.putFloat(player.getSpeed());
        buffer.put(player.getDisplayName().getBytes(StandardCharsets.UTF_8));
    }

    /** <p>Вставляет данные сущности-мутанта в {@link ByteBuffer} снапшота:<br>
     * 1. Опкод типа сущности (1 байт)<br>
     * 2. Опкод типа мутанта (1 байт)<br>
     * 3. Имя мутанта ({@code String}, 1-16 байт, но строгого потолка (пока) нет :р)</p>
     * @param buffer {@link ByteBuffer}, в который ведётся запись
     * @param mutant Мутант - элемент снапшота*/
    private static void putMutantData(ByteBuffer buffer, Mutant mutant) {
        buffer.put(PacketOpcodes.OP_ENTITY_TYPE_MUTANT);
        buffer.put(mutant.getOpcode());
        buffer.put(mutant.getDisplayName().getBytes(StandardCharsets.UTF_8));
    }

    /** <p>Вставляет данные сущности-аномалии в {@link ByteBuffer} снапшота:<br>
     * 1. Опкод типа сущности (1 байт)<br>
     * 2. Опкод типа аномалии (1 байт)<br>
     * @param buffer {@link ByteBuffer}, в который ведётся запись
     * @param anomaly Аномалия - элемент снапшота*/
    private static void putAnomalyData(ByteBuffer buffer, Anomaly anomaly) {
        buffer.put(PacketOpcodes.OP_ENTITY_TYPE_ANOMALY);
        buffer.put(anomaly.getOpcode());
    }

    /** <p>Формирует тело пакета для отправки {@code EntityDeathEvent} выбранному игроку</p>
     * <p>Тело пакета:<br>
     * 1. Опкод (1 байт)
     * 2. UUID сущности (16 байт)</p>*/
    public static byte[] encodeDeathEvent(EntityDeathEvent event) {
        int packetSize = 1 + 16;
        ByteBuffer buffer = ByteBuffer.allocate(packetSize);

        buffer.put(PacketOpcodes.OP_S2C_ENTITY_DEATH);
        buffer.putLong(event.uuid().getMostSignificantBits());
        buffer.putLong(event.uuid().getLeastSignificantBits());

        return buffer.array();
    }
}
