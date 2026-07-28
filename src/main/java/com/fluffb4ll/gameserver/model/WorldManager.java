package com.fluffb4ll.gameserver.model;

import com.fluffb4ll.gameserver.model.enums.ChunkState;
import com.fluffb4ll.gameserver.model.records.ChunkCoordinate;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WorldManager {
    private final Map<ChunkCoordinate, MapChunk> chunks = new ConcurrentHashMap<>();

    // TODO: вынести в отдельный конфиг
    // размер чанка в юнитах
    private static final int CHUNK_SIZE = 100;

    // размеры мира в чанках
    private static final int WORLD_WIDTH_IN_CHUNKS = 10;
    private static final int WORLD_HEIGHT_IN_CHUNKS = 10;

    @PostConstruct
    public void initializeWorld() {
        System.out.println("Начало инициализации карты...");

        for (int cx = 0; cx < WORLD_WIDTH_IN_CHUNKS; cx++)
            for (int cy = 0; cy < WORLD_HEIGHT_IN_CHUNKS; cy++) {
                float startX = cx * CHUNK_SIZE;
                float startY = cy * CHUNK_SIZE;

                Vector2D startPoint = new Vector2D(startX, startY);
                Vector2D endPoint = new Vector2D(startX + CHUNK_SIZE, startY + CHUNK_SIZE);

                ChunkCoordinate coordinate = new ChunkCoordinate(cx, cy);

                // TODO: парсить тикрейт из конфига
                MapChunk chunk = new MapChunk(UUID.randomUUID(), startPoint, endPoint, ChunkState.ACTIVE);

                chunks.put(coordinate, chunk);
            }

        System.out.println("Карта успешно инициализирована. Заселено чанков: "
                + (WORLD_HEIGHT_IN_CHUNKS * WORLD_WIDTH_IN_CHUNKS)
        );
    }

    public MapChunk getChunk(ChunkCoordinate coordinate) {
        return chunks.get(coordinate);
    }

    public List<MapChunk> getAllChunks() {
        return new ArrayList<>(chunks.values());
    }

    public MapChunk getChunkByPosition(Vector2D pos) {
        int cx = (int) Math.floor(pos.x / CHUNK_SIZE);
        int cy = (int) Math.floor(pos.y / CHUNK_SIZE);
        return chunks.get(new ChunkCoordinate(cx, cy));
    }

    /**
     * Перемещает сущность из старого чанка в новый на основе её новых координат.
     * @param entity Сущность, которую нужно переместить
     * @param currChunk Чанк, в котором находится сущность в данный момент
     * @param newPos Новая позиция сущности
     */
    public void moveEntity(LivingEntity entity, MapChunk currChunk, Vector2D newPos) {
        if (currChunk.contains(newPos) && entity.move(newPos))
            return;

        MapChunk newChunk = getChunkByPosition(newPos);

        if (newChunk == null) {
            handleOutOfBoundsTravel(entity);
            return;
        }

        if (!entity.move(newPos))
            return;

        removeEntityFromChunk(entity, currChunk);
        addEntityToChunk(entity, newChunk);
    }

    private void removeEntityFromChunk(BaseEntity entity, MapChunk chunk) {
        if (entity instanceof Player player)
            chunk.removePlayer(player);
        else if (entity instanceof Mutant mutant)
            chunk.removeMutant(mutant);
        else if (entity instanceof Anomaly anomaly)
            chunk.removeAnomaly(anomaly);
    }

    private void addEntityToChunk(BaseEntity entity, MapChunk chunk) {
        if (entity instanceof Player player)
            chunk.addPlayer(player);
        else if (entity instanceof Mutant mutant)
            chunk.addMutant(mutant);
        else if (entity instanceof Anomaly anomaly)
            chunk.addAnomaly(anomaly);
    }

    private void handleOutOfBoundsTravel(LivingEntity entity) {
        // TODO: добавить обработку выхода за границы карты
        System.out.println("Сущность " + entity.getUuid() + " попыталась выйти за пределы карты!");
    }

    // TODO: изменять лоды чанков в зависимости от близости игроков
    public void updateChunkLODs() throws Exception {
        return;
    }
}
