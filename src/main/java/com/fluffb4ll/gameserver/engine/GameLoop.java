package com.fluffb4ll.gameserver.engine;

import com.fluffb4ll.gameserver.engine.entities.LivingEntity;
import com.fluffb4ll.gameserver.engine.entities.Player;
import com.fluffb4ll.gameserver.handler.WebSocketHandler;
import com.fluffb4ll.gameserver.util.PacketEncoder;
import com.fluffb4ll.gameserver.util.WorldLogger;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GameLoop {
    // TODO: парсить из конфига
    private static final int TICK_RATE = 20;

    private final WorldManager worldManager;
    private final WebSocketHandler wsHandler;

    // ритм сервера
    private final ScheduledExecutorService heartbeat = Executors.newSingleThreadScheduledExecutor();

    // кастомный тред-пул с понятными именами потоков для удобства отладки
    private final ExecutorService workerPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() - 2,
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "chunk-worker-" + threadNumber.getAndIncrement());
                }
            }
    );

    private long tickCount = 0;

    public GameLoop(WorldManager worldManager, WebSocketHandler wsHandler) {
        this.worldManager = worldManager;
        this.wsHandler = wsHandler;
    }

    public void start() {
        heartbeat.scheduleAtFixedRate(this::mainTick, 0, 1000 / TICK_RATE, TimeUnit.MILLISECONDS);
        System.out.format("[GameLoop]: Запуск Game Loop на %d TPS%n", TICK_RATE);
    }

    private void mainTick() {
        try {
            tickCount++;

            // логгируем заголовок тика раз в секунду, чтобы не засирать консоль
            boolean shouldLogTick = (tickCount % TICK_RATE == 0);
            if (shouldLogTick) {
                WorldLogger.logTickHeader(tickCount);
                worldManager.updateChunkLODs();
            }

            // обработка очереди пакетов
            processPlayerPackets();

            // обработка чанков
            int submittedTasks = tickChunks(shouldLogTick);

            // отправка пакетов игрокам
            broadcastData();

            if (shouldLogTick && submittedTasks == 0) {
                System.out.println("[GameLoop]: Нет активных чанков для обработки (все в SLEEPING или карту не заселили).");
            }

        } catch (Exception e) {
            System.err.format("[GameLoop]: Ошибка при выполнении тика номер %d: %s%n", tickCount, e.getMessage());
        }
    }

    private void processPlayerPackets() {
        List<CompletableFuture<Void>> tasks = worldManager.getPlayers().stream()
                .map(
                        player -> CompletableFuture.runAsync(
                                () -> player.processInboundQueue(worldManager), workerPool))
                .toList();

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
    }

    private int tickChunks(boolean shouldLogTick) {
        List<MapChunk> activeChunks = worldManager.getAllChunks().stream()
                .filter(this::shouldTickChunk)
                .toList();

        List<CompletableFuture<Void>> tasks = activeChunks.stream()
                .map(chunk -> CompletableFuture.runAsync(
                        () -> chunk.tick(tickCount, shouldLogTick, 1f / TICK_RATE, worldManager), workerPool))
                .toList();

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        return activeChunks.size();
    }

    private void broadcastData() {
        List<Player> players = worldManager.getPlayers();
        if (players.isEmpty()) { return; }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (Player player : players) {
                executor.submit(() -> processBroadcast(player));
            }
        }

        wsHandler.closeDeadSessions();
    }

    private void processBroadcast(Player player) {
        WebSocketSession session = wsHandler.getSessions().get(player.getUuid());
        // TODO: объединить ивенты и снапшот в один пакет?
        // вывод ивентов
        Queue<byte[]> outputQueue = player.getOutboundEventsQueue();
        while (!outputQueue.isEmpty())
            try {
                byte[] packet = outputQueue.poll();
                session.sendMessage(new BinaryMessage(packet));
            } catch (IOException _) {
                wsHandler.addDeadSession(player.getUuid());
                return;
            }

        List<LivingEntity> visibleEntities = worldManager.findLivingEntitiesInNearbyChunks(player);

        // вывод снапшота
        byte[] snapshotData = PacketEncoder.createWorldSnapshot(player, visibleEntities);

        try {
            session.sendMessage(new BinaryMessage(snapshotData));
        } catch (IOException _) {
            wsHandler.addDeadSession(player.getUuid());
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
        workerPool.shutdown();
    }
}