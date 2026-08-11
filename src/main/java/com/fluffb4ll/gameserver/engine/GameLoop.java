package com.fluffb4ll.gameserver.engine;

import com.fluffb4ll.gameserver.model.MapChunk;
import com.fluffb4ll.gameserver.model.WorldManager;
import com.fluffb4ll.gameserver.util.WorldLogger;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GameLoop {
    // TODO: парсить из конфига
    private static final int TICK_RATE = 20;

    private final WorldManager worldManager;

    // ритм сервера
    private final ScheduledExecutorService heartbeat = Executors.newSingleThreadScheduledExecutor();

    // кастомный тред-пул с понятными именами потоков для удобства отладки
    private final ExecutorService chunkWorkerPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "chunk-worker-" + threadNumber.getAndIncrement());
                }
            }
    );

    private long tickCount = 0;

    public GameLoop(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public void start() {
        heartbeat.scheduleAtFixedRate(this::mainTick, 0, 1000 / TICK_RATE, TimeUnit.MILLISECONDS);
        System.out.format("[GameLoop]: Запуск Game Loop на %d TPS%n", TICK_RATE);
    }

    private void mainTick() {
        try {
            tickCount++;

            // логгируем заголовок тика раз в секунду, чтобы не засирать консоль
            boolean isSecondTick = (tickCount % TICK_RATE == 0);
            if (isSecondTick) {
                WorldLogger.logTickHeader(tickCount);
                worldManager.updateChunkLODs();
            }

            AtomicInteger submittedTasks = new AtomicInteger(0);

            worldManager.getAllChunks().forEach(chunk -> {
                if (shouldTickChunk(chunk)) {
                    submittedTasks.incrementAndGet();
                    chunkWorkerPool.submit(() -> {
                        // проверяем, какой именно поток забрал чанк в работу
                        // System.out.printf("[%s] Processing chunk %s%n", Thread.currentThread().getName(), chunk.getCoords());
                        chunk.tick(tickCount, isSecondTick, 1f / TICK_RATE, worldManager);
                    });
                }
            });

            if (isSecondTick && submittedTasks.get() == 0) {
                System.out.println("[GameLoop]: Нет активных чанков для обработки (все в SLEEPING или карту не заселили).");
            }

        } catch (Exception e) {
            System.err.format("[GameLoop]: Ошибка при выполнении тика номер %d: %s%n", tickCount, e.getMessage());
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