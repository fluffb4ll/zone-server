package com.fluffb4ll.gameserver.model;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.concurrent.*;

public class GameLoop {
    // TODO: парсить из конфига
    private static final int TICK_RATE = 20;

    private final WorldManager worldManager;

    // ритм сервера
    private final ScheduledExecutorService heartbeat = Executors.newSingleThreadScheduledExecutor();

    // пул потоков для обработки чанков
    private final ExecutorService chunkWorkerPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    private long tickCount = 0;

    public GameLoop(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @PostConstruct
    public void start() {
        heartbeat.scheduleAtFixedRate(this::mainTick, 0, 1000 / TICK_RATE, TimeUnit.MILLISECONDS);
        System.out.format("[GameLoop]: Запуск Game Loop на %s TPS", TICK_RATE);
    }

    private void mainTick() {
        try {
            tickCount++;

            if (tickCount % TICK_RATE == 0)
                worldManager.updateChunkLODs();

            worldManager.getAllChunks().forEach(chunk -> {
                if (shouldTickChunk(chunk))
                    chunkWorkerPool.submit(() -> {chunk.tick(tickCount);});
            });

        } catch (Exception e) {
            System.err.format("[GameLoop]: Ошибка при выполнении тика номер %s: %s%n", tickCount, e.getMessage());
        }
    }

    private boolean shouldTickChunk(MapChunk chunk) {
        return switch (chunk.getState()) {
            case ACTIVE -> true;
            case MACRO -> tickCount % TICK_RATE == 0;
            case SLEEPING -> false;
        };
    }

    @PreDestroy
    private void stop() {
        heartbeat.shutdown();
        chunkWorkerPool.shutdown();
    }
}
