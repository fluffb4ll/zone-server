package com.fluffb4ll.gameserver.service;

import com.fluffb4ll.gameserver.entity.PlayerEntity;
import com.fluffb4ll.gameserver.repository.PlayerRepository;
import com.fluffb4ll.gameserver.util.WorldLogger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Service
public class PlayerSaveService {
    private final PlayerRepository playerRepository;
    private final Semaphore semaphore = new Semaphore(5);
    private final int BATCH_SIZE = 100;

    public PlayerSaveService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Async
    public void savePlayerDataAsync(List<PlayerEntity> playerData) {
        List<List<PlayerEntity>> batches = new ArrayList<>();

        for (int i = 0; i <= playerData.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, playerData.size());
            batches.add(playerData.subList(i, end));
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (List<PlayerEntity> batch : batches)
                executor.submit(() -> saveDataBatch(batch));
        }
    }

    private void saveDataBatch(List<PlayerEntity> batch) {
        boolean acquired = false;
        try {
            acquired = semaphore.tryAcquire(10, TimeUnit.SECONDS);

            if (!acquired) {
                WorldLogger.logError("Не удалось получить доступ к базе данных для автосохранения: timeout");
                return;
            }

            playerRepository.saveAll(batch);
        } catch (InterruptedException e) {
            WorldLogger.logException("Прерван поток сохранения пачки", e);
        } catch (Exception e) {
            WorldLogger.logException("Ошибка при автосохранении пачки", e);
        }
        finally {
            if (acquired)
                semaphore.release();
        }
    }
}
