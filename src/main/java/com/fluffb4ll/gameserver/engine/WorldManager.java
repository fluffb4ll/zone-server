package com.fluffb4ll.gameserver.engine;

import com.fluffb4ll.gameserver.engine.entities.*;
import com.fluffb4ll.gameserver.handler.WebSocketHandler;
import com.fluffb4ll.gameserver.model.enums.ChunkState;
import com.fluffb4ll.gameserver.model.records.ChunkCoordinate;
import com.fluffb4ll.gameserver.engine.terrains.SpawnerTerrain;
import com.fluffb4ll.gameserver.engine.terrains.Terrain;
import com.fluffb4ll.gameserver.util.Vector2D;
import com.fluffb4ll.gameserver.util.WorldLogger;
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
    private final Map<UUID, Player> players = new ConcurrentHashMap<>();

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
                MapChunk chunk = new MapChunk(startPoint, endPoint, ChunkState.ACTIVE);

                chunks.put(coordinate, chunk);
            }

        // TODO: переписать в логгер
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

    public List<Player> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public boolean addPlayer(Player player) {
        return players.put(player.getUuid(), player) == null;
    }

    public boolean removePlayer(Player player) {
        return removePlayer(player.getUuid());
    }

    // TODO: заменить на подписку на ивент дисконнекта (в чанке в т.ч.)
    public boolean removePlayer(UUID uuid) {
        if (!players.containsKey(uuid))
            return false;

        Player player = players.get(uuid);
        getChunkByPosition(player.getPosition()).removePlayer(player);

        players.remove(uuid);
        return true;
    }

    /**
     * Перемещает сущность из старого чанка в новый на основе её новых координат.
     * @param entity Сущность, которую нужно переместить
     * @param currChunk Чанк, в котором находится сущность в данный момент
     * @param newPos Новая позиция сущности
     */
    public void moveEntity(LivingEntity entity, MapChunk currChunk, Vector2D newPos) {
        // TODO: переписать под EventBus?
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
        WorldLogger.logChunkMigration(entity.getUuid(), currChunk, newChunk);
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

    public void spawnEntity(BaseEntity entity) {
        MapChunk chunk = getChunkByPosition(entity.getPosition());
        if (chunk == null)
            return;

        switch (entity) {
            case Mutant mutant -> chunk.addMutant(mutant);
            case Anomaly anomaly -> chunk.addAnomaly(anomaly);
            case Player player ->  {
                players.put(player.getUuid(), player);
                chunk.addPlayer(player);
            }
            default -> {}
        }
    }

    public void spawnTerrain(Terrain terrain) {
        MapChunk chunk = getChunkByPosition(terrain.getPosition());
        if (chunk == null)
            return;

        switch (terrain) {
            case SpawnerTerrain spawner -> chunk.addSpawner(spawner);
            default -> {}
        }
    }

    public List<MapChunk> getNearbyChunks(LivingEntity entity) {
        Vector2D pos = entity.getPosition();
        List<MapChunk> chunks = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++)
                chunks.add(getChunkByPosition(new Vector2D(
                        pos.x + CHUNK_SIZE * dx,
                        pos.y + CHUNK_SIZE * dy
                )));
        return chunks;
    }

    public LivingEntity findLivingEntityInNearbyChunks(Player player, UUID id) {
        List<MapChunk> nearbyChunks = getNearbyChunks(player);
        if (nearbyChunks.isEmpty())
            return null;

        for (MapChunk chunk : nearbyChunks) {
            LivingEntity found = chunk.getAllEntitiesStream()
                    .filter(e -> e.getUuid().equals(id))
                    .findFirst()
                    .orElse(null);
            if (found != null)
                return found;
        }

        return null;
    }
}
