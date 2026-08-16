package com.fluffb4ll.gameserver.util;

import com.fluffb4ll.gameserver.engine.MapChunk;

import java.util.UUID;

public class WorldLogger {

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";

    public static void logChunkProcessing(String chunkCoord, int mutantsCount, int anomaliesCount) {
        String thread = Thread.currentThread().getName();
        System.out.printf("%s[%s]%s Чанк %s -> Мутантов: %d, Аномалий: %d%n",
                PURPLE, thread, RESET, chunkCoord, mutantsCount, anomaliesCount);
    }

    public static void logEntityMove(UUID entityId, float x, float y) {
        String thread = Thread.currentThread().getName();
        System.out.printf("%s[%s]%s [MOVE] %s -> Pos: (%.2f, %.2f)%n",
                GREEN, thread, RESET, entityId, x, y);
    }

    public static void logChunkMigration(UUID entityId, MapChunk fromChunk, MapChunk toChunk) {
        String thread = Thread.currentThread().getName();
        Vector2D chunk1Start = fromChunk.getStartPoint();
        Vector2D chunk2Start = toChunk.getStartPoint();
        System.out.printf("%s[%s]%s [MIGRATION] %s перешел из чанка %s; %s в %s; %s%n",
                YELLOW, thread, RESET, entityId, chunk1Start.x, chunk1Start.y,
                chunk2Start.x, chunk2Start.y);
    }

    public static void logAnomalyHit(String eDisplayName,
                                     UUID entityId,
                                     int hpLeft,
                                     String aDisplayName,
                                     UUID anomalyId) {
        String thread = Thread.currentThread().getName();
        System.out.printf("%s[%s]%s [DAMAGE] %s (%s) попал в %s (%s)! Осталось HP: %d%n",
                RED, thread, RESET, eDisplayName, entityId, aDisplayName, anomalyId, hpLeft);
    }

    public static void logTickHeader(long tickCount) {
        String thread = Thread.currentThread().getName();
        System.out.printf("%s[%s]%s Тик №%s%n",
                PURPLE, thread, RESET, tickCount);
    }
}