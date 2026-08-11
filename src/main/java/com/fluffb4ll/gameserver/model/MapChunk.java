package com.fluffb4ll.gameserver.model;

import com.fluffb4ll.gameserver.model.entities.*;
import com.fluffb4ll.gameserver.model.enums.ChunkState;
import com.fluffb4ll.gameserver.model.terrains.SpawnerTerrain;
import com.fluffb4ll.gameserver.util.Vector2D;
import com.fluffb4ll.gameserver.util.WorldLogger;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Представляет собой независимую область игрового мира,
 * используемую для оптимизации рендеринга и работы с памятью.
 */
public class MapChunk {
    private final UUID id;

    private final Vector2D startPoint;
    private final Vector2D endPoint;

    // сущности
    private final Set<Player> players;
    private final Set<Anomaly> anomalies;
    private final Set<Mutant> mutants;

    // террейны
    private final Set<SpawnerTerrain> spawners;

    private ChunkState chunkState;

    public MapChunk(Vector2D start, Vector2D end, ChunkState chunkState) {
        this.id = UUID.randomUUID();
        startPoint = start;
        endPoint = end;

        players = ConcurrentHashMap.newKeySet();
        anomalies = ConcurrentHashMap.newKeySet();
        mutants = ConcurrentHashMap.newKeySet();

        spawners = ConcurrentHashMap.newKeySet();

        this.chunkState = chunkState;
    }

    public UUID getId() {
        return id;
    }

    public Vector2D getStartPoint() {
        return startPoint;
    }

    public Vector2D getEndPoint() {
        return endPoint;
    }

    public Set<Player> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public Set<Anomaly> getAnomalies() {
        return Collections.unmodifiableSet(anomalies);
    }

    public Set<Mutant> getMutants() {
        return Collections.unmodifiableSet(mutants);
    }

    public Set<SpawnerTerrain> getSpawners() {
        return Collections.unmodifiableSet(spawners);
    }

    public Stream<BaseEntity> getAllEntitiesStream() {
        return Stream.concat(players.stream(),
                Stream.concat(mutants.stream(), anomalies.stream())
        );
    }

    public ChunkState getState() {
        return chunkState;
    }

    public boolean addPlayer(Player player) {
        return players.add(player);
    }

    public boolean addAnomaly(Anomaly anomaly) {
        return anomalies.add(anomaly);
    }

    public boolean addMutant(Mutant mutant) {
        return mutants.add(mutant);
    }

    public boolean addSpawner(SpawnerTerrain spawner) {
        return spawners.add(spawner);
    }

    public boolean removePlayer(Player player) {
        return players.remove(player);
    }

    public boolean removeAnomaly(Anomaly anomaly) {
        return anomalies.remove(anomaly);
    }

    public boolean removeMutant(Mutant mutant) {
        return mutants.remove(mutant);
    }

    public boolean removeSpawner(SpawnerTerrain spawner) {
        return spawners.remove(spawner);
    }

    public synchronized void setState(ChunkState chunkState) {
        this.chunkState = chunkState;
    }

    public boolean contains(Vector2D pos) {
        return pos.x >= startPoint.x && pos.x < endPoint.x &&
                pos.y >= startPoint.y && pos.y < endPoint.y;
    }

    /**
     * Обработчик состояния чанка за один тик
     * @param tickCount число тиков, прошедших со старта сервера
     * @param deltaTime время в секундах, прошедшее с прошлого тика
     * @param worldManager менеджер мира, используется для миграции сущностей
     */
    public void tick(long tickCount, boolean shouldLogStatus, float deltaTime, WorldManager worldManager) {
        if ((!anomalies.isEmpty() || !mutants.isEmpty()) && shouldLogStatus)
            WorldLogger.logChunkProcessing(String.format("%s %s", startPoint.x, startPoint.y), mutants.size(), anomalies.size());
        tickAnomalies(deltaTime);
        tickMutants(deltaTime, worldManager);
        tickSpawners(deltaTime);
        resolveCollisions();
        cleanUpDeadEntities();
    }

    private void tickAnomalies(float deltaTime) {
        for (Anomaly anomaly : anomalies)
            anomaly.update(deltaTime);
    }

    private void tickMutants(float deltaTime, WorldManager worldManager) {
        for (Mutant mutant : mutants) {
            if (!mutant.isAlive())
                continue;
            mutant.updateAI(deltaTime);
            Vector2D newPos = mutant.calculateNextPosition(deltaTime);
            worldManager.moveEntity(mutant, this, newPos);
        }
    }

    private void tickSpawners(float deltaTime) {
        for (SpawnerTerrain spawner : spawners)
            spawner.update(deltaTime);
    }

    private void resolveCollisions() {
        for (Anomaly anomaly : anomalies) {
            for (Player player : players)
                checkEntityAnomalyCollision(player, anomaly);
            for (Mutant mutant : mutants)
                checkEntityAnomalyCollision(mutant, anomaly);
        }
    }

    private void checkEntityAnomalyCollision(LivingEntity entity, Anomaly anomaly) {
        if (!entity.isAlive()) {
            anomaly.removeTarget(entity);
            return;
        }

        if (anomaly.isColliding(entity))
            anomaly.addTarget(entity);
        else
            anomaly.removeTarget(entity);
    }

    private void cleanUpDeadEntities() {
        mutants.removeIf(mutant -> !mutant.isAlive());
    }
}
